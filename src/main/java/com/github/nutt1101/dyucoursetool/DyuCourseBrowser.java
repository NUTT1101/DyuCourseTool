package com.github.nutt1101.dyucoursetool;

import com.github.nutt1101.dyucoursetool.modal.Course;
import com.github.nutt1101.dyucoursetool.modal.User;
import jakarta.annotation.PostConstruct;
import org.htmlunit.HttpMethod;
import org.htmlunit.WebClient;
import org.htmlunit.WebRequest;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
@PropertySource("classpath:api.properties")
public class DyuCourseBrowser extends WebClient {
    @Value("${api.login}")
    String loginApiLink;

    @PostConstruct
    void setup() {
        this.getOptions().setCssEnabled(false);
        this.getOptions().setJavaScriptEnabled(false);
    }

    public void login(User user) throws IOException {
        WebRequest request = new WebRequest(
                new URL(this.loginApiLink), HttpMethod.POST
        );

        request.setRequestBody(
                String.format(
                        "txt_userid=%s&pwd_word=%s",
                        user.getLoginParameter().getId(),
                        user.getLoginParameter().getPassword()
                )
        );


        HtmlPage page = this.getPage(request);
        HtmlForm form = page.getFormByName("mf");
        String headerLog = form.getInputByName("header_log").getValueAttribute();
        user.setHeaderLog(headerLog);
    }

    public Course getCourse(String courseId) throws IOException {
        WebRequest request = new WebRequest(
                new URL("http://cs.dyu.edu.tw/g_c_name.php?ad_ser=" + courseId),
                HttpMethod.GET
        );

        String str = this.getPage(request).getWebResponse().getContentAsString();
        str = str.replace(" ", "").trim();
        short credit = Short.parseShort(str.split("/")[0]);
        String courseName = str.split("/")[1];
        return Course.builder()
                .courseId(courseId)
                .courseName(courseName)
                .credit(credit)
                .build();
    }
}
