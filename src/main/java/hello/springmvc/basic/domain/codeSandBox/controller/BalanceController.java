package hello.springmvc.basic.domain.codeSandBox.controller;

import com.example.demo.database.Database;
import com.example.demo.database.Account;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/account")
public class BalanceController {

    private final Database db;
    private final Map<Long, Object> accountLocks = new ConcurrentHashMap<>();
    // 계좌별로 동기화를 위한 객체 사용소
    private final Map<Long, Boolean> depositing = new ConcurrentHashMap<>();
    // 입금이 동시에 중복처리되는걸 막기위한 map

    public BalanceController(Database db) {
        this.db = db;
    }

    // 계정별 락 객체를 얻거나 새로 생성
    private Object getLockForAccount(Long id) {
        return accountLocks.computeIfAbsent(id, key -> new Object());
    }

    /**
     * 잔고 조회 API
     */
    @GetMapping("{id}/balance")
    public Account balance(@PathVariable Long id) {
        return db.balance(id);
    }

    /**
     * 입금 API - 동시 입금 요청 시 실패 처리
     */
    @PostMapping("{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long id, @RequestBody Long amount) {
        if (amount <= 0) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "입금액은 0보다 커야 합니다."));
        }

        Object lock = getLockForAccount(id);

        synchronized (lock) {
            // 이미 입금 중인지 확인
            if (depositing.putIfAbsent(id, true) != null) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "이미 입금 처리 중입니다. 잠시 후 다시 시도하세요."));
            }

            try {
                Account currentAccount = db.balance(id);
                long newBalance = currentAccount.getBalance() + amount;
                Account updatedAccount = db.balance(id, newBalance);
                return ResponseEntity.ok(updatedAccount);
            } finally {
                // 입금 처리 완료
                depositing.remove(id);
            }
        }
    }

    /**
     * 출금 API - 잔액 부족 시 실패, 동시 출금 요청은 순차 처리
     */
    @PostMapping("{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long id, @RequestBody Long amount) {
        if (amount <= 0) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "출금액은 0보다 커야 합니다."));
        }

        Object lock = getLockForAccount(id);

        synchronized (lock) {
            Account currentAccount = db.balance(id);

            if (currentAccount.getBalance() < amount) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error", "잔액이 부족합니다.",
                                "currentBalance", currentAccount.getBalance(),
                                "requestedAmount", amount
                        ));
            }

            long newBalance = currentAccount.getBalance() - amount;
            Account updatedAccount = db.balance(id, newBalance);
            return ResponseEntity.ok(updatedAccount);
        }
    }
}
