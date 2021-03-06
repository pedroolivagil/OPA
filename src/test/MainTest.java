package test;

import com.olivadevelop.persistence.utils.*;

import java.util.List;

public class MainTest {
    static Logger<Test> logger = new Logger<>(Test.class);

    public static void main(String args[]) {
        logger.print("INIT TEST");
        Test tester = new Test();
        TestEntity testEntity = new TestEntity();
        /*testEntity.setId(122423);*/
        testEntity.setName("Pedro");
        testEntity.setApellido("Oliva");
        testEntity.setBool(true);
        testEntity.setPrecio(20.54);


        // lanzamos los testers
        /*tester.testQuery(testEntity);
        tester.testComplexQuery(testEntity);
        tester.testInsert(testEntity);
        tester.testUpdate(testEntity);
        tester.testComplexUpdate(testEntity);
        tester.testDelete(testEntity);*/
        TestController controller = new TestController();
        try {
            /*controller.create(testEntity);
            testEntity.setName("Olivadevelop SL");
            controller.update(testEntity);*/
            TestEntity ent = controller.read(981);
            logger.print(ent.toString());
            logger.print("---------------------------");
            ent.setId(123);
            controller.update(ent);
            logger.print("---------------------------");

            List<TestEntity> all = controller.readAll();
            for (TestEntity t : all) {
                logger.print(t.toString());
            }
            logger.print("---------------------------");
            JoinQuery jq = new JoinQuery();
            jq.addJoin(TestEntity.class, TestEntity2.class);

            QueryBuilder.Query query = new QueryBuilder.Query();
            query.from(TestEntity.class).join(jq).where("id = 929");
            TestEntity ent1 = controller.read(query);
            logger.print(ent1.toString());
        } catch (OlivaDevelopException e) {
            logger.error(e);
        }
        logger.print("Finish");
    }
}

class Test {
    Logger<Test> logger = new Logger<>(Test.class);

    void testQuery(TestEntity entity) {
        logger.print(".testQuery() is IN");
        QueryBuilder.Query qb = new QueryBuilder.Query();
        try {
            qb.from(TestEntity.class);
        } catch (OlivaDevelopException e) {
            e.printStackTrace();
        }
        logger.print(qb.toString());
    }

    void testComplexQuery(TestEntity entity) {
        logger.print(".testComplexQuery() is IN");
        QueryBuilder.Query qb = new QueryBuilder.Query();
        try {
            qb.from(TestEntity.class).where("id = 23").and("name LIKE 'Pedro'").or("besugo = false").distinct();
        } catch (OlivaDevelopException e) {
            e.printStackTrace();
        }
        logger.print(qb.toString());
    }

    void testInsert(TestEntity entity) {
        logger.print(".testInsert() is IN");
        QueryBuilder.Insert qb = new QueryBuilder.Insert();
        try {
            qb.from(TestEntity.class).values(entity);
        } catch (OlivaDevelopException | IllegalAccessException e) {
            e.printStackTrace();
        }
        logger.print(qb.toString());
    }

    void testUpdate(TestEntity entity) {
        logger.print(".testUpdate() is IN");
        QueryBuilder.Update qb = new QueryBuilder.Update();
        try {
            qb.from(TestEntity.class).values(entity);
        } catch (OlivaDevelopException | IllegalAccessException e) {
            e.printStackTrace();
        }
        logger.print(qb.toString());
    }

    void testComplexUpdate(TestEntity entity) {
        logger.print(".testUpdate() is IN");
        QueryBuilder.Update qb = new QueryBuilder.Update();
        try {
            FieldData<String, Object> pk = Utils.getPkFromEntity(entity);
            qb.from(entity.getClass());
            qb.values(entity);
            qb.where(pk.getKey().concat(" = ").concat(pk.getValueAsString()));
        } catch (OlivaDevelopException | IllegalAccessException e) {
            e.printStackTrace();
        }
        logger.print(qb.toString());
    }

    void testDelete(TestEntity entity) {
        logger.print(".testDelete() is IN");
        QueryBuilder.Delete qb = new QueryBuilder.Delete();
        try {
            qb.from(TestEntity.class).where("id = 1").and("lol = 'xD'");
        } catch (OlivaDevelopException e) {
            e.printStackTrace();
        }
        logger.print(qb.toString());
    }
}
