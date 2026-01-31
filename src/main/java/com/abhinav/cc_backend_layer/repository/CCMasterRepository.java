package com.abhinav.cc_backend_layer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.abhinav.cc_backend_layer.model.AmountPerMonth;
import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.model.CCMasterKey;

@Repository
public interface CCMasterRepository extends JpaRepository<CCMaster, CCMasterKey> {

	List<CCMaster> findAllByKeyCode(String code);
	
	//List<CCMaster> findAllByKeyCodeAndUsername(String code, String username);
	
	@Query("SELECT c FROM CC_MASTER_TEST c WHERE c.key.code = :code AND c.key.username = :username")
	List<CCMaster> findAllByKeyCodeAndUsername(@Param("code") String code, @Param("username") String username);

	List<CCMaster> findAllByKeyStmtMonthYear(String monthYear);
	
	//List<CCMaster> findAllByKeyStmtMonthYearAndUsername(String monthYear,String username);
	
	@Query("SELECT c FROM CC_MASTER_TEST c WHERE c.key.stmtMonthYear = :monthYear AND c.key.username = :username")
	List<CCMaster> findAllByKeyStmtMonthYearAndUsername(@Param("monthYear") String monthYear, @Param("username") String username);

	//@Query(value = "select CAST(SUBSTR(STMTMONTHYEAR,0,2) as INTEGER) as mm, SUBSTR(STMTMONTHYEAR,3,6) as yyyy, SUM(CAST(TOTALAMT as INTEGER)) as amount from  CC_MASTER_TEST where SUBSTR(STMTMONTHYEAR,3,6) = ? group by mm,yyyy order by mm", nativeQuery = true)
	@Query(value = "select CAST(SUBSTRING(STMTMONTHYEAR, 1, 2) as UNSIGNED) as mm, SUBSTRING(STMTMONTHYEAR, 3, 6) as yyyy, SUM(CAST(TOTALAMT as UNSIGNED)) as amount from `freedb_cc-database`.CC_MASTER_TEST cmt where SUBSTRING(STMTMONTHYEAR, 3, 6) = ? and cmt.CODE <> 'PNB02' group by mm,yyyy order by mm", nativeQuery = true)
	List<AmountPerMonth> getAmountPerMonth(String year);
	
	@Query(value = "select CAST(SUBSTRING(STMTMONTHYEAR, 1, 2) as UNSIGNED) as mm, SUBSTRING(STMTMONTHYEAR, 3, 6) as yyyy, SUM(CAST(TOTALAMT as UNSIGNED)) as amount from `freedb_cc-database`.CC_MASTER_TEST cmt where SUBSTRING(STMTMONTHYEAR, 3, 6) = ? and cmt.CODE <> 'PNB02' and username = ? group by mm,yyyy order by mm", nativeQuery = true)
	List<AmountPerMonth> getAmountPerMonthByUser(String year, String username);

	//@Query(value = "select c2.name, SUM(CAST(c1.TOTALAMT as INTEGER)) as amount from  CC_MASTER_TEST c1, CC_MASTER_NAMES c2 where c1.code=c2.code and substring(c1.STMTMONTHYEAR,3,7)= ? group by c2.name", nativeQuery = true)
	@Query(value = "select c2.name, SUM(CAST(c1.TOTALAMT as UNSIGNED)) as amount from `freedb_cc-database`.CC_MASTER_TEST c1, `freedb_cc-database`.CC_MASTER_NAMES c2 where c1.code = c2.code and substring(c1.STMTMONTHYEAR, 3, 7)= ? and c2.name <> 'PNB Car Loan' group by c2.name order by c2.name", nativeQuery = true)
	List<AmountPerMonth> getAmountPerCard(String year);
	
	@Query(value = "select c2.name, SUM(CAST(c1.TOTALAMT as UNSIGNED)) as amount from `freedb_cc-database`.CC_MASTER_TEST c1, `freedb_cc-database`.CC_MASTER_NAMES c2 where c1.code = c2.code and substring(c1.STMTMONTHYEAR, 3, 7)= ? and c2.name <> 'PNB Car Loan' and username = ? group by c2.name order by c2.name", nativeQuery = true)
	List<AmountPerMonth> getAmountPerCardByUser(String year, String username);
	
	List<CCMaster> findByCurrentStatusNot(String currentStatus);

	//List<CCMaster> findByUsernameAndCurrentStatusNot(String username, String currentStatus);
	
	@Query("SELECT c FROM CC_MASTER_TEST c WHERE c.key.username = :username AND c.currentStatus <> :status")
	List<CCMaster> findByUsernameAndCurrentStatusNot(@Param("username") String username, @Param("status") String status);
	
	List<CCMaster> findAllByKeyUsername(String username);
}
