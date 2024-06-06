package com.github.nutt1101.dyucoursetool;

import com.github.nutt1101.dyucoursetool.modal.Course;
import com.github.nutt1101.dyucoursetool.modal.User;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
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
import java.util.Optional;

@Component
@PropertySource("classpath:api.properties")
public class DyuCourseBrowser extends WebClient {
    @Value("${api.login}")
    String loginApiLink;

    @Value("${api.course-info}")
    String courseInfoLink;

    String semesterYear;
    String semester;
    List<Course> coursePool;

    @PostConstruct
    void setup() throws IOException {
        this.getOptions().setCssEnabled(false);
        this.getOptions().setJavaScriptEnabled(false);
        List<String> currentSemester = this.getCurrentSemester();
        semesterYear = currentSemester.get(0);
        semester = currentSemester.get(1);
        coursePool = new ArrayList<>();
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

        HtmlDivision htmlDiv = responsePage.querySelector("div.minor_items");
        List<HtmlSpan> htmlSpans = htmlDiv.getChildNodes().stream().filter(e -> e instanceof HtmlSpan).map(e -> (HtmlSpan) e).toList();
        if (htmlSpans.isEmpty()) {
            throw new RuntimeException("user information error");
        }

        htmlSpans.forEach(htmlInput -> {
            HtmlLabel label = (HtmlLabel) htmlInput.getElementsByTagName("label").get(0);
            if (label == null) return;
            System.out.println(label.getAttribute("title"));
        });
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

    @SneakyThrows
    public Course getCourse(String courseId) throws IOException {
        Optional<Course> courseOptional = coursePool.stream()
                .filter(e -> e.getCourseId().equalsIgnoreCase(courseId))
                .findFirst();
        if (courseOptional.isPresent()) {
            return courseOptional.get();
        }

        WebRequest request = new WebRequest(
                new URL(this.courseInfoLink),
                HttpMethod.POST
        );

        request.setRequestBody(
                this.prepareCourseInformationBody(semesterYear, semester, courseId)
        );

        HtmlPage page = this.getPage(request);

        HtmlDivision divRow = page.querySelector("div[class=row]");

        if (divRow == null) {
            throw new RuntimeException("can not get course div information");
        }

        HtmlDivision courseCreditDiv  = divRow.querySelector("div[class=td2]");
        HtmlDivision courseNameDiv = divRow.querySelector("div[class=td4]");

        String courseName = courseNameDiv.getTextContent();
        String courseCredit = courseCreditDiv.getTextContent();
        short credit = (short) Character.getNumericValue(
                courseCredit.charAt(0)
        );

        Thread.sleep(500);

        Course course = Course.builder()
                .courseName(courseName)
                .courseId(courseId)
                .credit(credit)
                .build();
        coursePool.add(course);
        return course;
    }

    String prepareCourseInformationBody(String semesterYear, String semester, String courseId) {
        return String.format(
                "smye=%s&smty=%s&cour_id=%s", semesterYear, semester, courseId
        );
    }

    List<String> getCurrentSemester() throws IOException {
        WebRequest request = new WebRequest(
                new URL("http://syl.dyu.edu.tw/3000.php"),
                HttpMethod.GET
        );

        HtmlPage page = this.getPage(request);
        HtmlInput semesterYearElement = page.querySelector("input[id=smye]");
        
        if (semesterYearElement == null) {
            throw new RuntimeException("can not get semester year information");
        }

        String semesterYear  = semesterYearElement.getValue();

        HtmlSelect semesterSelectElement = page.querySelector("select[id=smty]");

        if (semesterSelectElement == null) {
            throw new RuntimeException("can not get semester information");
        }

        List<HtmlOption> selectedOptions = semesterSelectElement.getSelectedOptions();
        if (selectedOptions.size() != 1) {
            throw new RuntimeException("can not get semester information because has multi semester data");
        }

        HtmlOption semesterHtmlOption = selectedOptions.get(0);
        String semester = semesterHtmlOption.getValueAttribute();

        return List.of(semesterYear, semester);
    }
}
