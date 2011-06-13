/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import vn.hus.nlp.tagger.TaggerOptions;
import vn.hus.nlp.tagger.VietnameseMaxentTagger;

/**
 *
 * @author banhbaochay
 */
public class Test {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String t = "[Tuy nhiên] [viên chức] [này] [cho biết] <org> [công ty] [Toyota] </org> [chưa] [quyết định] [chống] [lại] [lệnh] [phạt] [16,4 triệu] [đô la] [do] [viên chức] [an toàn] [của] <per> [Mỹ] </per> [tuyên phạt] [hôm] [thứ hai] [hay] [không] . ";
        TaggerOptions.PLAIN_TEXT_FORMAT = true;
        TaggerOptions.UNDERSCORE = true;
        String s1 = "Anh Nam hiện đang sống ở Hà Nội và làm việc cho công ty VNDF, đến tháng 10 anh chuyển vào Nha Trang để làm việc cho công ty OSS";
        System.out.println(ConvertText.vnTagger(s1, ConvertText.ONLY_SEGMENT));
        String s = "[Và] [khi] [còn] [là] [sinh viên] <per> [Kovats] </per> [đã] [gây] [chấn động] [trong] [trường] [khi] [dám] [bỏ] [tiền] [ra] [mua] [lại] "
                + "[cả] <org> [công ty] [kinh doanh] [Disotheh] </org> [nổi tiếng] [ở] <loc> [thành phố] [Viên] </loc>";
        Sentence sent = new Sentence(ConvertText.convertForSentence(s));
        System.out.println(sent.toJSRE());
    }
}
