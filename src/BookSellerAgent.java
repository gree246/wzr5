/*
 *  Klasa agenta sprzedawcy książek.
 *  Sprzedawca dysponuje katalogiem książek oraz dwoma klasami zachowań:
 *  - OfferRequestsServer - obsługa odpowiedzi na oferty klientów
 *  - PurchaseOrdersServer - obsługa zamówienia klienta
 *
 *  Argumenty projektu (NETBEANS: project properties/run/arguments):
 *  -agents seller1:BookSellerAgent();seller2:BookSellerAgent();buyer1:BookBuyerAgent(Zamek) -gui
 */
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import java.util.*;
import java.lang.*;


public class BookSellerAgent extends Agent
{
  // Katalog książek na sprzedaż:
  private Hashtable catalogue;

  // Inicjalizacja klasy agenta:
  protected void setup()
  {
    // Tworzenie katalogu książek jako tablicy rozproszonej
    catalogue = new Hashtable();

    Random randomGenerator = new Random();    // generator liczb losowych

    catalogue.put("Zamek", 300+randomGenerator.nextInt(200));       // nazwa książki jako klucz, cena jako wartość
    catalogue.put("Proces", 250+randomGenerator.nextInt(70));
    catalogue.put("Opowiadania", 110+randomGenerator.nextInt(50));
    catalogue.put("Zadanie_4c", 60+randomGenerator.nextInt(70));
    catalogue.put("Niedziela, późny wieczór", 250+randomGenerator.nextInt(80));

    doWait(2000);                     // czekaj 2 sekundy

    System.out.println("Witam! Agent-sprzedawca (wersja c 2016/17) "+getAID().getName()+" jest gotów do handlu!");

    // Dodanie zachowania obsługującego odpowiedzi na oferty klientów (kupujących książki):
    addBehaviour(new OfferRequestsServer());

    // Dodanie zachowania obsługującego zamówienie klienta:
    addBehaviour(new PurchaseOrdersServer());
  }

  // Metoda realizująca zakończenie pracy agenta:
  protected void takeDown()
  {
    System.out.println("Agent-sprzedawca (wersja c 2016/17) "+getAID().getName()+" zakończył działalność.");
  }


  /**
    Inner class OfferRequestsServer.
    This is the behaviour used by Book-seller agents to serve incoming requests
    for offer from buyer agents.
    If the requested book is in the local catalogue the seller agent replies
    with a PROPOSE message specifying the price. Otherwise a REFUSE message is
    sent back.
    */
    class OfferRequestsServer extends CyclicBehaviour
    {
      public void action()
      {
        // Tworzenie szablonu wiadomości (wstępne określenie tego, co powinna zawierać wiadomość)
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
        // Próba odbioru wiadomości zgodnej z szablonem:
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {  // jeśli nadeszła wiadomość zgodna z ustalonym wcześniej szablonem
          String title = msg.getContent();  // odczytanie tytułu zamawianej książki

          System.out.println("Agent-sprzedawca "+getAID().getName()+" otrzymał wiadomość: "+
                   title);
          ACLMessage reply = msg.createReply();               // tworzenie wiadomości - odpowiedzi
          Integer price = (Integer) catalogue.get(title);     // ustalenie ceny dla podanego tytułu
          if (price != null) {                                // jeśli taki tytuł jest dostępny
            reply.setPerformative(ACLMessage.PROPOSE);            // ustalenie typu wiadomości (propozycja)
            reply.setContent(String.valueOf(price.intValue()));   // umieszczenie ceny w polu zawartości (content)
            System.out.println("Agent-sprzedawca "+getAID().getName()+" odpowiada: "+
                   price.intValue());
          }
          else {                                              // jeśli tytuł niedostępny
            // The requested book is NOT available for sale.
            reply.setPerformative(ACLMessage.REFUSE);         // ustalenie typu wiadomości (odmowa)
            reply.setContent("tytuł niestety niedostępny");                  // treść wiadomości
          }
          myAgent.send(reply);                                // wysłanie odpowiedzi
        }
        else                       // jeśli wiadomość nie nadeszła, lub była niezgodna z szablonem
        {
          block();                 // blokada metody action() dopóki nie pojawi się nowa wiadomość
        }
      }
    } // Koniec klasy wewnętrznej będącej rozszerzeniem klasy CyclicBehaviour


    class PurchaseOrdersServer extends CyclicBehaviour
    {
      public void action()
      {
        ACLMessage msg = myAgent.receive();

        if ((msg != null)&&(msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL))
        {
          // Message received. Process it          
          ACLMessage reply = msg.createReply();
          String title = msg.getContent();
          reply.setPerformative(ACLMessage.INFORM);
          System.out.println("Agent-sprzedawca (wersja c 2016/17) "+getAID().getName()+" sprzedał tytuł: "+title);
          myAgent.send(reply);
        }
        else if((msg != null)&&(msg.getPerformative() == ACLMessage.PROPOSE)){
            String title = msg.getContent();
            int price = Integer.parseInt(title.split("->")[2]);
            int old_price=Integer.parseInt(title.split("->")[1]);
            title=title.split("->")[0];
            System.out.println("Agent-sprzedawca "+getAID().getName()+" otrzymał propozycje kupna za: "+
                   price);
            ACLMessage reply = msg.createReply();
            reply.setContent(String.valueOf((int)((price+old_price)/2)));
            System.out.println("Agent-sprzedawca "+getAID().getName()+" odpowiada: "+
                   String.valueOf((int)((price+old_price)/2)));
            myAgent.send(reply);
            
            
        }
      }
    } // Koniec klasy wewnętrznej będącej rozszerzeniem klasy CyclicBehaviour
} // Koniec klasy będącej rozszerzeniem klasy Agent