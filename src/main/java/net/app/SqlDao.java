package net.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import javax.persistence.EntityManager;

@Component
public class SqlDao {
    @Autowired
    EntityManager em;

    public List<?> listFoodMoneyDateOfPayment(int departmentId, String sumKey, String tableName) {
        String s = "SELECT SUM(%s) AS money, CONCAT(YEAR(apply_time), '-', MONTH(apply_time)) AS yearmonth " +
                "FROM (%s) WHERE department_id = %d GROUP BY yearmonth";
        String sql = String.format(s, sumKey, tableName, departmentId);
        System.out.println(sql);

        List<?> test = em.createNativeQuery(sql).getResultList();

        return test;
    }
}