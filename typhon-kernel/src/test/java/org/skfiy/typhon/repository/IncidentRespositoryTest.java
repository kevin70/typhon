//package org.skfiy.typhon.repository;
//
//import javax.inject.Inject;
//
//import org.skfiy.typhon.TestBase;
//import org.skfiy.typhon.domain.Incident;
//import org.skfiy.typhon.spi.RoleProvider;
//import org.testng.annotations.Test;
//
//public class IncidentRespositoryTest extends TestBase {
//
//    @Inject
//    protected PlayerEventRepository pleventReposy;
//    
//    @Inject
//    protected RoleProvider roleProvider;
//    @Inject 
//    protected RoleRepository roleRepository;
//    @Test
//    public void testSave() {
//        Incident event = new Incident();
//        event.setUid(5);
//        event.setEventName("ss");
//        event.setData("{uid:5,name:kevin,level:5}");
//        event.setCreatedTime(System.currentTimeMillis());
//        pleventReposy.save(event);
//        roleProvider.addFriend(1);          
//        roleRepository.findRoles("he");
//        //  
//    }
//}
