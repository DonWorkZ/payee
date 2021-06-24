package com.fdmgroup.pilotbank2.repo;

import com.fdmgroup.pilotbank2.models.Account;
import com.fdmgroup.pilotbank2.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {

	List<Transaction> findAllByAccountOrderByTransactionDateDesc(Account account);
	List<Transaction> findAllByPayee_PayeeId(Long payeeId);

	@Query("select t from Transaction t where t.account LIKE :account AND t.transactionDate >= :monthStart AND t.transactionDate <= :monthEnd ORDER BY t.transactionDate")
	List<Transaction> findMonthlyStatementByAccount(@Param("account") Account account, @Param("monthStart") LocalDateTime monthStart, @Param("monthEnd") LocalDateTime monthEnd);
}
