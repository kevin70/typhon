package item;

import org.skfiy.typhon.dobj.ComplexItemDobj;
import org.skfiy.typhon.dobj.SimpleItemDobj;
import org.skfiy.typhon.domain.item.Subitem;
import org.skfiy.typhon.script.Script;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionUtils;
/**
*
* @author Administrator
*/
public class SocietyItemScript implements Script {

   @Override
   public Object invoke(Session session, Object obj) {
       Object[] array = (Object[]) obj;
       SimpleItemDobj itemDobj = (SimpleItemDobj) array[0];
       int count = (int) array[1];
       SessionUtils.incrementSocietyMoney(count * (int) itemDobj.getAnnex());
       return null;
   }
   
}