package test;

import com.olivadevelop.persistence.controllers.BasicController;

public class TestController extends BasicController<TestEntity> {


    public TestController() {
        super(TestEntity.class);
    }


}
