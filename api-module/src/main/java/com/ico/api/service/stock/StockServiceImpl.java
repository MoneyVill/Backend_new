package com.ico.api.service.stock;

import com.ico.api.dto.stock.*;
import com.ico.api.service.transaction.TransactionService;
import com.ico.api.user.JwtTokenProvider;
import com.ico.api.util.Formatter;
import com.ico.core.entity.*;
import com.ico.core.exception.CustomException;
import com.ico.core.exception.ErrorCode;
import com.ico.core.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 투지 이슈 Service
 *
 * @author 변윤경
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService{
    private final StudentRepository studentRepository;
    private final NationRepository nationRepository;
    private final StockRepository stockRepository;
    private final InvestRepository investRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TransactionService transactionService;
    private final StockItemRepository stockItemRepository;

    /**
     * 교사 투자 이슈 목록 조회
     *
     * @return 교사화면의 투자 이슈 정보
     */
    @Override
    public StockTeacherResDto getIssueTeacher(HttpServletRequest request, Long stockItemId) {
        Long nationId = jwtTokenProvider.getNation(jwtTokenProvider.parseJwt(request));

        // Retrieve StockItem
        StockItem stockItem = stockItemRepository.findById(stockItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_STOCK));

        // Verify that the StockItem belongs to the Nation
        if (!stockItem.getNation().getId().equals(nationId)) {
            throw new CustomException(ErrorCode.NOT_FOUND_STOCK);
        }

        // Retrieve issues for the StockItem
        List<StockColDto> issues = getIssues(stockItemId);

        // Build response
        return StockTeacherResDto.builder()
                .stock(stockItem.getName())
                .tradingStart(stockItem.getTradingStart())
                .tradingEnd(stockItem.getTradingEnd())
                .issue(issues)
                .build();
    }


    /**
     * 학생 투자 이슈 목록 조회
     * @return 학생 화면의 투자 이슈 정보
     */
    @Override
    public StockStudentResDto getIssueStudent(HttpServletRequest request, Long stockItemId) {
        String token = jwtTokenProvider.parseJwt(request);
        Long nationId = jwtTokenProvider.getNation(token);
        Long studentId = jwtTokenProvider.getId(token);

        // Retrieve StockItem
        StockItem stockItem = stockItemRepository.findById(stockItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_STOCK));

        // Verify that the StockItem belongs to the Nation
        if (!stockItem.getNation().getId().equals(nationId)) {
            throw new CustomException(ErrorCode.NOT_FOUND_STOCK);
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Optional<Invest> invest = investRepository.findByStudentIdAndStockItemId(studentId, stockItemId);
        log.info("매수 여부 확인");

        StockMyResDto myStock = new StockMyResDto();
        log.info("학생 매수 정보");

        if (invest.isPresent()) {
            log.info("매수 이력 있음");
            myStock.setPrice(invest.get().getPrice());
            myStock.setAmount(invest.get().getAmount());
        } else {
            log.info("매수 이력 없음");
            myStock.setPrice(0);
            myStock.setAmount(0);
        }

        // Build response
        StockStudentResDto res = new StockStudentResDto();
        res.setAccount(student.getAccount());
        res.setStock(stockItem.getName());
        res.setTradingStart(stockItem.getTradingStart());
        res.setTradingEnd(stockItem.getTradingEnd());
        res.setMyStock(myStock);
        res.setIssue(getIssues(stockItemId));

        return res;
    }


    /**
     * 투자 이슈 등록
     * @param dto 지수, 내일의 투자 이슈
     */
    @Override
    public void uploadIssue(HttpServletRequest request, StockUploadReqDto dto) {
        Long nationId = jwtTokenProvider.getNation(jwtTokenProvider.parseJwt(request));

        // Retrieve StockItem
        StockItem stockItem = stockItemRepository.findById(dto.getStockItemId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_STOCK));

        // Check if StockItem belongs to Nation
        if (!stockItem.getNation().getId().equals(nationId)) {
            throw new CustomException(ErrorCode.NOT_FOUND_STOCK);
        }

        // Calculate new price
        double value = dto.getPrice();
        if (dto.getAmount() > 0) {
            value += dto.getAmount() / 100.0 * value;
        } else {
            value -= Math.abs(dto.getAmount()) / 100.0 * value;
        }
        double res = Math.round(value * 100.0) / 100.0;

        // Save new issue
        Stock stock = Stock.builder()
                .date(LocalDateTime.now())
                .amount(res)
                .content(dto.getContent())
                .stockItem(stockItem)
                .build();
        stockRepository.save(stock);

    }


    /**
     * 투자 종목 삭제
     *
     * @param request
     */
    @Override
    public void deleteStock(HttpServletRequest request, Long stockItemId) {
        Long nationId = jwtTokenProvider.getNation(jwtTokenProvider.parseJwt(request));

        // Retrieve StockItem
        StockItem stockItem = stockItemRepository.findById(stockItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_STOCK));

        // Verify that the StockItem belongs to the Nation
        if (!stockItem.getNation().getId().equals(nationId)) {
            throw new CustomException(ErrorCode.NOT_FOUND_STOCK);
        }

        // Retrieve investments for the StockItem
        List<Invest> invests = investRepository.findAllByStockItemId(stockItemId);

        // Get the latest stock data
        List<Stock> stockList = stockRepository.findAllByStockItemIdOrderByIdDesc(stockItemId);
        if (stockList.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_STOCK);
        }

        // Latest price
        double price = stockList.get(0).getAmount();
        log.info("매도지수 : " + price);

        // Process students' sell orders
        for (Invest invest : invests) {
            long studentId = invest.getStudent().getId();
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            double purchasePrice = invest.getPrice();
            log.info("매수지수 : " + purchasePrice);

            double changeRate = (price - purchasePrice) / purchasePrice;
            log.info("수익률 : " + changeRate);

            int amount = invest.getAmount();
            int salePrice = (int) (amount + amount * changeRate);
            log.info("매도 이익 : " + salePrice);

            // Deposit sale amount to student's account
            student.setAccount(student.getAccount() + salePrice);
            studentRepository.save(student);

            // Record transaction
            StringBuilder title = new StringBuilder("수익률 : ");
            title.append((int) (changeRate * 100)).append("%");
            transactionService.addTransactionDeposit(studentId, stockItem.getName() + " 증권", salePrice, title.toString());

            // Delete investment record
            investRepository.delete(invest);
        }

        // Delete stock data
        stockRepository.deleteAll(stockList);

        // Delete the StockItem
        stockItemRepository.delete(stockItem);
    }


    /**
     * 국가 정보, 투자 종목 여부 검사
     * @param nationId 국가ID
     * @return 국가 객체
     */
    private Nation validCheckNationStock(Long nationId) {
        Nation nation = nationRepository.findById(nationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NATION_NOT_FOUND));

        // Check if there are any StockItems associated with the Nation
        List<StockItem> stockItems = stockItemRepository.findAllByNationId(nationId);
        if (stockItems.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_STOCK);
        }

        return nation;
    }


    /**
     * 투자 이슈 목록
     * @param stockItemId
     * @return 투자 이슈 목록 조회
     */
    private List<StockColDto> getIssues(Long stockItemId){
        List<StockColDto> issuesRes = new ArrayList<>();
        List<Stock> issues = stockRepository.findAllByStockItemIdOrderByIdDesc(stockItemId);
        for (Stock issue : issues) {
            StockColDto col = new StockColDto();
            col.setContent(issue.getContent());
            col.setAmount(issue.getAmount());
            col.setDate(issue.getDate().format(Formatter.date));
            issuesRes.add(col);
        }

        return issuesRes;
    }

    @Override
    public List<StockItemResDto> getStockItems(HttpServletRequest request) {
        Long nationId = jwtTokenProvider.getNation(jwtTokenProvider.parseJwt(request));

        List<StockItem> stockItems = stockItemRepository.findAllByNationId(nationId);
        return stockItems.stream()
                .map(stockItem -> StockItemResDto.builder()
                        .id(stockItem.getId())
                        .name(stockItem.getName())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 투자 목록 조회
     */
    @Override
    public List<StockItemResDto> getAllStocks(HttpServletRequest request) {
        Long nationId = jwtTokenProvider.getNation(jwtTokenProvider.parseJwt(request));

        // 해당 나라의 모든 투자 종목 조회
        List<StockItem> stockItems = stockItemRepository.findAllByNationId(nationId);

        // StockItemResDto로 변환하여 반환
        return stockItems.stream()
                .map(stockItem -> StockItemResDto.builder()
                        .id(stockItem.getId())
                        .name(stockItem.getName())
                        .tradingStart(stockItem.getTradingStart())
                        .tradingEnd(stockItem.getTradingEnd())
                        .build())
                .collect(Collectors.toList());
    }

}

