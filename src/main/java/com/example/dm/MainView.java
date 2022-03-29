package com.example.dm;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Route
public class MainView extends VerticalLayout {

    public MainView() throws IOException {

        //Область поиска
        TextField textField = new TextField();
        textField.setPlaceholder("ssau.ru");
        //убрать
//        textField.setValue("imc.ssau.ru");
        Button findButton = new Button("Начать");
        findButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout searchArea = new HorizontalLayout(textField, findButton);

        add(new H2(" "));

        //Вывод ссылок

        UnorderedList content = new UnorderedList();
        UnorderedList content2 = new UnorderedList();

        Details details = new Details();
        details.setOpened(true);
        details.addThemeVariants(DetailsVariant.FILLED);
        details.setSummaryText("Внутренние ссылки: ");

        Details details2 = new Details();
        details2.setOpened(true);
        details2.addThemeVariants(DetailsVariant.FILLED);
        details2.setSummaryText("Битые ссылки: ");

        HorizontalLayout hl = new HorizontalLayout(details, details2);
        hl.setPadding(true);


        VerticalLayout vr = new VerticalLayout(searchArea, new H3(""), hl);
        vr.setAlignItems(Alignment.CENTER);
        add(vr);

        Scroller scroller = new Scroller(content);
        scroller.setScrollDirection(Scroller.ScrollDirection.HORIZONTAL);

        Scroller scroller2 = new Scroller(content2);
        scroller2.setScrollDirection(Scroller.ScrollDirection.HORIZONTAL);
        findButton.addClickShortcut(Key.ENTER);

        findButton.addClickListener(clickEvent -> {
            try {
                Links ln = new Links(textField.getValue());
                content.removeAll();
                content2.removeAll();
                String html = ln.parseHTML(textField.getValue());
                ln.getURLs(html);
                for(String s : ln.URLs) {
                    content.add(new ListItem(s));
                }
                scroller.setContent(content);
                details.setContent(scroller);
                details.setSummaryText("Внутренние ссылки: " + ln.URLs.size());



                for(String s : ln.brokenURLs) {
                    content2.add(new ListItem(s));
                }

                scroller2.setContent(content2);
                details2.setContent(scroller2);
                details2.setSummaryText("Битые ссылки: " + ln.brokenURLs.size());

//                TextArea tx = new TextArea();
//                tx.setValue(ln.parseHTML(textField.getValue()));
//                add(tx);


            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
