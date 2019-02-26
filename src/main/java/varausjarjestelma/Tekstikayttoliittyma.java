package varausjarjestelma;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Tekstikayttoliittyma {

    @Autowired
    Varausjarjestelma varausjarjestelma;

    public void kaynnista(Scanner lukija) throws SQLException {

        while (true) {
            System.out.println("Komennot: ");
            System.out.println(" x - lopeta");
            System.out.println(" 1 - lisaa huone");
            System.out.println(" 2 - listaa huoneet");
            System.out.println(" 3 - hae huoneita");
            System.out.println(" 4 - lisaa varaus");
            System.out.println(" 5 - listaa varaukset");
            System.out.println(" 6 - tilastoja");
            System.out.println("");

            String komento = lukija.nextLine();
            if (komento.equals("x")) {
                break;
            }

            if (komento.equals("1")) {
                lisaaHuone(lukija);
            } else if (komento.equals("2")) {
                System.out.println("");
                listaaHuoneet();
            } else if (komento.equals("3")) {
                haeHuoneita(lukija);
            } else if (komento.equals("4")) {
                lisaaVaraus(lukija);
            } else if (komento.equals("5")) {
                listaaVaraukset();
            } else if (komento.equals("6")) {
                tilastoja(lukija);
            }
        }
    }

    private void lisaaHuone(Scanner s) throws SQLException {
        System.out.println("Lisätään huone");
        System.out.println("");

        System.out.println("Minkä tyyppinen huone on?");
        String tyyppi = s.nextLine();
        System.out.println("Mikä huoneen numeroksi asetetaan?");
        int numero = Integer.valueOf(s.nextLine());
        System.out.println("Kuinka monta euroa huone maksaa yöltä?");
        int hinta = Integer.valueOf(s.nextLine());

        this.varausjarjestelma.lisaaHuone(numero, hinta, tyyppi);
        System.out.println("");

    }

    private void listaaHuoneet() throws SQLException {
        System.out.println("Listataan huoneet");
        System.out.println("");

        List<Huone> huoneet = this.varausjarjestelma.listaaHuoneet();

        for (Huone h : huoneet) {
            System.out.println(h);
        }

        if (huoneet.isEmpty()) {
            System.out.println("Ei huoneita järjestelmässä.");
        }
        System.out.println("");

    }

    private void haeHuoneita(Scanner s) throws SQLException {
        System.out.println("Haetaan huoneita");
        System.out.println("");

        System.out.println("Milloin varaus alkaisi (yyyy-MM-dd)?");;
        Date alku = Date.valueOf(LocalDate.parse(s.nextLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        Date loppu = null;
        while (true) {
            System.out.println("Milloin varaus loppuisi (yyyy-MM-dd)?");
            loppu = Date.valueOf(LocalDate.parse(s.nextLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            if (loppu.before(alku) || loppu.equals(alku)) {
                System.out.println("Epäkelpo päivämäärä.");
            } else {
                break;
            }
        }
        System.out.println("Minkä tyyppinen huone? (tyhjä = ei rajausta)");
        String tyyppi = s.nextLine();
        System.out.println("Minkä hintainen korkeintaan? (tyhjä = ei rajausta)");
        String maksimihinta = s.nextLine();

        // Etsitään vapaat huoneet yllä olevien (käyttäjän syöttämien) kriteerien mukaisesti
        List<Huone> vapaatHuoneet = this.varausjarjestelma.haeVapaatHuoneet(alku, loppu, tyyppi, maksimihinta);

        // Ja tulostetaan vapaat huoneet, jos niitä löytyi
        if (vapaatHuoneet.isEmpty()) {
            System.out.println("Ei vapaita huoneita.");
            System.out.println("");
        } else {
            System.out.println("Vapaat huoneet: ");
            Collections.sort(vapaatHuoneet);
            for (int i = 0; i < vapaatHuoneet.size(); i++) {
                System.out.println(vapaatHuoneet.get(i));
            }
            System.out.println("");
        }

    }

    private void lisaaVaraus(Scanner s) throws SQLException {
        System.out.println("Haetaan huoneita");
        System.out.println("");

        System.out.println("Milloin varaus alkaisi (yyyy-MM-dd)?");;
        Date alku = Date.valueOf(LocalDate.parse(s.nextLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        Date loppu = null;
        while (true) {
            System.out.println("Milloin varaus loppuisi (yyyy-MM-dd)?");
            loppu = Date.valueOf(LocalDate.parse(s.nextLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            if (loppu.before(alku) || loppu.equals(alku)) {
                System.out.println("Epäkelpo päivämäärä.");
            } else {
                break;
            }
        }

        System.out.println("Minkä tyyppinen huone? (tyhjä = ei rajausta)");

        String tyyppi = s.nextLine();
        System.out.println("Minkä hintainen korkeintaan? (tyhjä = ei rajausta)");
        String maksimihinta = s.nextLine();

        // Etsitään vapaat huoneet yllä olevien (käyttäjän syöttämien) kriteerien mukaisesti
        List<Huone> vapaatHuoneet = this.varausjarjestelma.haeVapaatHuoneet(alku, loppu, tyyppi, maksimihinta);

        // Mikäli huoneita ei ole vapaana, ohjelma tulostaa seuraavan viestin
        // ja varauksen lisääminen loppuu.
        // Muulloin, ohjelma kertoo vapaiden huoneiden lukumäärän. 
        if (vapaatHuoneet.isEmpty()) {
            System.out.println("Ei vapaita huoneita.");
            System.out.println("");
            return;
        } else {
            System.out.println("Huoneita vapaana: " + vapaatHuoneet.size());
            System.out.println("");
        }

        // Tämän jälkeen kysytään varattavien huoneiden lukumäärää
        // luvuksi tulee hyväksyä vain sopiva luku, eli vähintään 1
        // ja enintään niin monta kuin huoneita oli vapaana.
        int huoneita = -1;
        while (true) {
            System.out.println("Montako huonetta varataan?");
            huoneita = Integer.valueOf(s.nextLine());
            System.out.println("");
            if (huoneita >= 1 && huoneita <= vapaatHuoneet.size()) {
                break;
            }

            System.out.println("Epäkelpo huoneiden lukumäärä.");
        }

        // tämän jälkeen kysytään lisävarusteet
        
        List<String> lisavarusteet = new ArrayList<>();
        while (true) {
            System.out.println("Syötä lisävaruste, tyhjä lopettaa");
            String lisavaruste = s.nextLine();
            if (lisavaruste.isEmpty()) {
                break;
            }

            lisavarusteet.add(lisavaruste);
        }
         
        // ja lopuksi varaajan tiedot
        System.out.println("Syötä varaajan nimi:");
        String nimi = s.nextLine();
        System.out.println("Syötä varaajan puhelinnumero:");
        String puhelinnumero = s.nextLine();
        System.out.println("Syötä varaajan sähköpostiosoite:");
        String sahkoposti = s.nextLine();

        Asiakas a = new Asiakas(nimi, puhelinnumero, sahkoposti);
        this.varausjarjestelma.lisaaVaraus(alku, loppu, a, vapaatHuoneet, huoneita, lisavarusteet);
        System.out.println("");
    }

    private void listaaVaraukset() throws SQLException {
        System.out.println("Listataan varaukset");
        System.out.println("");

        this.varausjarjestelma.tulostaVaraukset();
      
    }

    private void tilastoja(Scanner lukija) throws SQLException {
        System.out.println("Mitä tilastoja tulostetaan?");
        System.out.println("");

        // tilastoja pyydettäessä käyttäjältä kysytään tilasto
        System.out.println(" 1 - Suosituimmat lisävarusteet");
        System.out.println(" 2 - Parhaat asiakkaat");
        System.out.println(" 3 - Varausprosentti huoneittain");
        System.out.println(" 4 - Varausprosentti huonetyypeittäin");

        System.out.println("Syötä komento: ");
        int komento = Integer.valueOf(lukija.nextLine());

        if (komento == 1) {
            suosituimmatLisavarusteet();
        } else if (komento == 2) {
            parhaatAsiakkaat();
        } else if (komento == 3) {
            varausprosenttiHuoneittain(lukija);
        } else if (komento == 4) {
            varausprosenttiHuonetyypeittain(lukija);
        }
    }

    private void suosituimmatLisavarusteet() throws SQLException {
        System.out.println("Tulostetaan suosituimmat lisävarusteet");
        System.out.println("");
        
        this.varausjarjestelma.tulostaLisavarusteet();
        System.out.println("");
    }

    private void parhaatAsiakkaat() throws SQLException {
        System.out.println("Tulostetaan parhaat asiakkaat");
        System.out.println("");
        
        this.varausjarjestelma.tulostaAsiakkaat();
        System.out.println("");
    }
    // TÄTÄ METODIA EI EHDITTY TOTEUTTAA
    private void varausprosenttiHuoneittain(Scanner lukija) {
        System.out.println("Tulostetaan varausprosentti huoneittain");
        System.out.println("");

        System.out.println("Mistä lähtien tarkastellaan?");
        LocalDateTime alku = LocalDateTime.parse(lukija.nextLine() + "-01 " + "16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("Mihin asti tarkastellaan?");
        LocalDateTime loppu = LocalDateTime.parse(lukija.nextLine() + "-01 " + "10:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // alla esimerkkitulostus
        System.out.println("Tulostetaan varausprosentti huoneittain");
        System.out.println("Excelsior, 604, 119 euroa, 0.0%");
        System.out.println("Excelsior, 605, 119 euroa, 0.0%");
        System.out.println("Superior, 705, 159 euroa, 22.8%");
        System.out.println("Commodore, 128, 229 euroa, 62.8%");
    }

    // TÄTÄ METODIA EI EHDITTY TOTEUTTAA
    private void varausprosenttiHuonetyypeittain(Scanner lukija) {
        System.out.println("Tulostetaan varausprosentti huonetyypeittäin");
        System.out.println("");

        System.out.println("Mistä lähtien tarkastellaan?");
        LocalDateTime alku = LocalDateTime.parse(lukija.nextLine() + "-01 " + "16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("Mihin asti tarkastellaan?");
        LocalDateTime loppu = LocalDateTime.parse(lukija.nextLine() + "-01 " + "10:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // alla esimerkkitulostus
        System.out.println("Tulostetaan varausprosentti huonetyypeittän");
        System.out.println("Excelsior, 0.0%");
        System.out.println("Superior, 22.8%");
        System.out.println("Commodore, 62.8%");
    }

}
