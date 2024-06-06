package com.github.nutt1101.dyucoursetool;

import com.github.nutt1101.dyucoursetool.modal.Course;
import com.github.nutt1101.dyucoursetool.modal.User;
import jakarta.annotation.PostConstruct;
import org.htmlunit.HttpMethod;
import org.htmlunit.WebClient;
import org.htmlunit.WebRequest;
import org.htmlunit.html.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    WebRequest prepareLoginRequest(User user) throws MalformedURLException {
        WebRequest r = new WebRequest(
                new URL(this.loginApiLink), HttpMethod.POST
        );
        r.setRequestBody(
                String.format(
                        "txt_userid=%s&pwd_word=%s",
                        user.getLoginParameter().getId(),
                        user.getLoginParameter().getPassword()
                ));
        return r;
    }

    public void login(User user) throws IOException {
        WebRequest request;

        HtmlPage responsePage;
        HtmlForm responseForm;

        String forwardLink;

        request = this.prepareLoginRequest(user);
        responsePage = this.getPage(request);
        responseForm = this.getInformationForm(responsePage, "mf");

        if (responseForm == null || responseForm.getActionAttribute().equalsIgnoreCase("error")) {
            throw new RuntimeException("login exception");
        }

        do {
            forwardLink = responseForm.getActionAttribute();

            if (!this.isValidURL(forwardLink)) {
                forwardLink = String.format(
                        "http://%s/%s", responsePage.getUrl().getHost(), forwardLink
                );
            }

            request.setUrl(new URL(forwardLink));
            request.setRequestBody(
                    this.convertFormToRequestBody(responseForm)
            );

            responsePage = this.getPage(request);
            responseForm = this.getInformationForm(responsePage, "mf", "myform");
        } while (responseForm != null);

//        HtmlDivision htmlDiv = page.querySelector("div.minor_items");
//        List<HtmlSpan> htmlSpans = htmlDiv.getChildNodes().stream().filter(e -> e instanceof HtmlSpan).map(e -> (HtmlSpan) e).toList();
//        if (htmlSpans.isEmpty()) {
//            throw new RuntimeException("user information error");
//        }
//
//        htmlSpans.forEach(htmlInput -> {
//            HtmlLabel label = (HtmlLabel) htmlInput.getElementsByTagName("label").get(0);
//            if (label == null) return;
//            System.out.println(label.getAttribute("title"));
//        });
    }

    HtmlForm getInformationForm(HtmlPage htmlPage, String... ids) {
        return htmlPage.getForms().stream()
                .filter(
                        e -> Arrays.stream(ids).anyMatch(
                                id -> e.getNameAttribute().equalsIgnoreCase(id)
                        )
                ).findFirst()
                .orElse(null);
    }

    String convertFormToRequestBody(HtmlForm htmlForm) {
        List<HtmlInput> inputs = htmlForm.getChildNodes().stream()
                .filter(e -> e instanceof HtmlInput)
                .map(e -> (HtmlInput) e).toList();

        List<String> bodies = new ArrayList<>();

        inputs.forEach(htmlInput -> bodies.add(
                String.format(
                        "%s=%s", htmlInput.getId(), htmlInput.getValue()
                )
        ));

        return String.join("&", bodies);
    }

    boolean isValidURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public Course getCourse(String courseId) throws IOException {
        WebRequest request = new WebRequest(
                new URL("http://syl.dyu.edu.tw/sl_cour.php" + courseId),
                HttpMethod.GET
        );

        // TODO: recode

        return Course.builder()
                .build();
    }
}
