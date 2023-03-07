package org.example;

import javax.mail.*;
import javax.mail.internet.AddressException;
import java.util.*;

public class ForensicPathologist {
    static Session session;
    static Store store;

    public static void init() throws Exception {
        session = Session.getDefaultInstance(new Properties());
        store = session.getStore(new URLName("mbox:"));
        store.connect();
    }

    // search given terms
    public static List<String> termSearch(List<String> terms) throws Exception {
        List<String> inBoxes = FileHelper.getListOfInboxes();
        List<String> output = new ArrayList<>();

        for (String box : inBoxes) {
            Folder inbox = getFolder(store, box);
            Message[] messages = inbox.search(QueryProvider.getTermSearch(terms));
            for (Message message : messages) {
                try {
                    output.add(message.getFrom()[0].toString() + " " + message.getSentDate());
                } catch (AddressException ad) {
                    String date = "";
                    String name = "";
                    for (Enumeration<Header> e = message.getAllHeaders(); e.hasMoreElements(); ) {
                        Header h = e.nextElement();
                        if (h.getName().equals("From"))
                            name = h.getValue();
                        if (h.getName().equals("Date"))
                            date = h.getValue();
                    }
                    output.add(name + " " + date);
                }
            }
            inbox.close(false);
        }
        close();
        return output;
    }

    private static Folder getFolder(Store store, String inBox) throws MessagingException {
        Folder inbox = store.getFolder("enron/" + inBox);
        inbox.open(Folder.READ_ONLY);
        return inbox;
    }

    public static List<String> addressSearch(String firstName, String lastName) throws Exception {
        Session session = Session.getDefaultInstance(new Properties());
        Store store = session.getStore(new URLName("mbox:"));
        store.connect();

        List<String> inBoxes = FileHelper.getListOfInboxes();
        Set<String> output = new HashSet<>();

        for (String box : inBoxes) {
            Folder inbox = getFolder(store, box);
            Message[] messages = inbox.search(QueryProvider.getAddressSearch(firstName, lastName));
            for (Message message : messages) {
                try {
                    for (Enumeration<Header> e = message.getAllHeaders(); e.hasMoreElements(); ) {
                        Header h = e.nextElement();
                        if (h.getValue().equals(firstName + " " + lastName)
                                || h.getValue().equals(lastName + " " + firstName)
                                || h.getValue().equals(firstName + ", " + lastName)
                                || h.getValue().equals(lastName + ", " + firstName)) {
                            if (h.getName().equalsIgnoreCase("X-From"))
                                output.add(message.getFrom()[0].toString());
                            if (h.getName().equalsIgnoreCase("X-To"))
                                output.add(message.getHeader("To")[0].toString());
                        }
                    }

                } catch (MessagingException | NullPointerException ex) {
                    ex.printStackTrace();
                }
            }
            inbox.close(false);
        }
        close();
        return output.stream().toList();
    }

    public static List<String> interactionSearch(String person1, String person2) throws Exception {
        Session session = Session.getDefaultInstance(new Properties());
        Store store = session.getStore(new URLName("mbox:"));
        store.connect();

        List<String> inBoxes = FileHelper.getListOfInboxes();
        Set<String> output = new HashSet<>();

        for (String box : inBoxes) {
            Folder inbox = getFolder(store, box);
            Message[] messages = inbox.search(QueryProvider.getInteractionSearch(person1, person2));
            for (Message message : messages) {
                try {
                    if ((message.getFrom()[0].toString().equalsIgnoreCase(person1)
                            && message.getHeader("To")[0].toString().equalsIgnoreCase(person2)))
                        output.add(person1 + " -> " + person2 + "  [Subject : " + message.getSubject() + "] "
                                + message.getSentDate());
                    else
                        output.add(person2 + " -> " + person1 + "  [Subject : " + message.getSubject() + "] "
                                + message.getSentDate());
                } catch (MessagingException | NullPointerException ex) {
                    ex.printStackTrace();
                }
            }
            inbox.close(false);
        }
        close();
        return output.stream().toList();
    }

    public static void close() throws Exception {
        store.close();
    }
}
