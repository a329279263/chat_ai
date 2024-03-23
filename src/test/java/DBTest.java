import cn.autumn.chat.domain.ChatMessageRecord;
import io.ebean.DB;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/11 10:14
 * @version: 1.0
 */
public class DBTest {
    public static void main(String[] args) {
        System.out.println(DB.find(ChatMessageRecord.class, 1));
    }
}
