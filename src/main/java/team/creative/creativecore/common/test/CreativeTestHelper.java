package team.creative.creativecore.common.test;

public class CreativeTestHelper {
    
    public void assertTrue(boolean value, String failMessage, Object... args) throws CreativeTestException {
        if (!value)
            throw new CreativeTestException(String.format(failMessage, args));
    }
    
}
