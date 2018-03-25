package test;

import com.olivadevelop.persistence.utils.OlivaDevelopException;
import com.olivadevelop.persistence.utils.QueryBuilder;

public class MainTest {

    public static void main(String args[]) {
        Test tester = new Test();
        TestEntity testEntity = new TestEntity();
        testEntity.setId(122423);
        testEntity.setName("Pedro");
        testEntity.setApellido("Oliva");
        testEntity.setBool(true);
        testEntity.setPrecio(20.54);

        // lanzamos los testers
        tester.testQuery(testEntity);
        tester.testComplexQuery(testEntity);
        tester.testInsert(testEntity);
    }
}

class Test {
    void testQuery(TestEntity entity) {
        System.out.println(".testQuery() is IN");
        QueryBuilder.Query qb = new QueryBuilder.Query();
        try {
            qb.from(TestEntity.class).find();
        } catch (OlivaDevelopException e) {
            e.printStackTrace();
        }
        System.out.println(qb.toString());
    }

    void testComplexQuery(TestEntity entity) {
        System.out.println(".testComplexQuery() is IN");
        QueryBuilder.Query qb = new QueryBuilder.Query();
        try {
            qb.from(TestEntity.class).find().where("id = 23").and("name LIKE 'Pedro'").distinct();
        } catch (OlivaDevelopException e) {
            e.printStackTrace();
        }
        System.out.println(qb.toString());
    }

    void testInsert(TestEntity entity) {
        System.out.println(".testInsert() is IN");
        QueryBuilder.Insert qb = new QueryBuilder.Insert();
        try {
            qb.from(TestEntity.class).values(entity);
        } catch (OlivaDevelopException | IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println(qb.toString());
    }
}
