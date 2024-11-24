package com.ico.api.service.stock;

import com.ico.api.service.transaction.TransactionService;
import com.ico.api.user.JwtTokenProvider;
import com.ico.core.entity.*;
import com.ico.core.exception.CustomException;
import com.ico.core.exception.ErrorCode;
import com.ico.core.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author 변윤경
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvestServiceImpl implements InvestService{
    private final StockItemRepository stockItemRepository;
    private final StudentRepository studentRepository;
    private final InvestRepository investRepository;
    private final NationRepository nationRepository;
    private final TransactionService transactionService;
    private final JwtTokenProvider jwtTokenProvider;
    private final StockRepository stockRepository;

    /**
     * 주식 매수
     *
     * @param price 현재 지수
     * @param amount 매수 금액
     */
    @Override
    public void buyStock(HttpServletRequest request, Long stockItemId, double price, int amount) {
        String token = jwtTokenProvider.parseJwt(request);
        Long studentId = jwtTokenProvider.getId(token);

        // Retrieve Student
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // Retrieve StockItem
        StockItem stockItem = stockItemRepository.findById(stockItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_STOCK));

        // Validate trading time
        LocalTime currentTime = LocalTime.now();
        if (currentTime.isBefore(stockItem.getTradingStart()) || currentTime.isAfter(stockItem.getTradingEnd())) {
            throw new CustomException(ErrorCode.NOT_TRADING_TIME);
        }

        // Check if student has already invested in this stock
        investRepository.findByStudentIdAndStockItemId(studentId, stockItemId).ifPresent(i -> {
            throw new CustomException(ErrorCode.ALREADY_HAVE_STOCK);
        });

        // Check student's account balance
        if (amount > student.getAccount()) {
            throw new CustomException(ErrorCode.LOW_BALANCE);
        }

        // Deduct amount from student's account
        student.setAccount(student.getAccount() - amount);
        studentRepository.save(student);

        // Save investment
        Invest invest = Invest.builder()
                .student(student)
                .stockItem(stockItem)
                .price(price)
                .amount(amount)
                .date(LocalDateTime.now())
                .build();
        investRepository.save(invest);

        // Add transaction record
        transactionService.addTransactionWithdraw(
                stockItem.getNation().getTitle() + " 증권",
                studentId,
                amount,
                stockItem.getName() + " 지수 " + price
        );
    }


    /**
     * 주식 매도
     */
    @Transactional
    @Override
    public void sellStock(HttpServletRequest request, Long stockItemId) {
        String token = jwtTokenProvider.parseJwt(request);
        Long nationId = jwtTokenProvider.getNation(token);
        Long studentId = jwtTokenProvider.getId(token);


        // 학생 유효검사
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 매수 내역 확인
        Invest invest = investRepository.findByStudentId(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_INVESET));

        // 거래 가능 시간 확인
        Nation nation = nationRepository.findById(student.getNation().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_STOCK));
//        LocalTime currentTime = LocalTime.now();
//        if(currentTime.isAfter(nation.getTrading_end()) || currentTime.isBefore(nation.getTrading_start())){
//            throw new CustomException(ErrorCode.NOT_TRADING_TIME);
//        }

        // 국가의 주식 데이터가 없을 경우
        List<Stock> stockList = stockRepository.findAllByStockItemIdOrderByIdDesc(stockItemId);
        if(stockList.isEmpty()){
            throw new CustomException(ErrorCode.NOT_FOUND_STOCK);
        }

        // 수익률 계산
        double price = stockList.get(0).getAmount();
        log.info("매도지수 : " + price);

        double purchasePrice = invest.getPrice();
        log.info("매수지수 : " + purchasePrice);

        double changeRate = (price - purchasePrice) / purchasePrice;
        log.info("수익률 : " + changeRate);

        // 매도 금액 계산(소수점 내림 적용)
        int amount = invest.getAmount();
        int salePrice = (int) (amount + amount * changeRate);
        log.info("매도 이익 : " + salePrice);

        // 매도 금액 입금
        student.setAccount(student.getAccount() + salePrice);
        studentRepository.save(student);

        // 거래 내역 기록
        StringBuilder title = new StringBuilder("수익률 : ");
        title.append((int)(changeRate * 100)).append("%");
        transactionService.addTransactionDeposit(studentId, nation.getTitle()+" 증권", salePrice, String.valueOf(title));

        // 매수 이력 삭제
        investRepository.delete(invest);
    }

}
