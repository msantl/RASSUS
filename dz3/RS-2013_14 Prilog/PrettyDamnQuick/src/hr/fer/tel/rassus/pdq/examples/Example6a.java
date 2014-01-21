/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.fer.tel.rassus.pdq.examples;

import com.perfdynamics.pdq.Methods;
import com.perfdynamics.pdq.Node;
import com.perfdynamics.pdq.PDQ;
import com.perfdynamics.pdq.QDiscipline;

/**
 *
 * @author Kreso
 */
public class Example6a {

    public static void main(String[] args) {
        PDQ pdq = new PDQ();

        final float L = 3f; // Ucestalost dolazaka zahtjeva u rep cekanja L = 3 upit/min
        final float S = 2f; // Prosjecno vrijeme posluzivanja zahtjeva S = 2 min/upit

        // Pomocne varijable za odredivanje imena cvorova i repova u PDQ mrezi
        String nName;
        String cName;

        int N = 10; // Broj paralelnih posluzitelja s privatnim repom

        // Postavljanje pocetnih postavki PDQ sustava
        pdq.Init("Aplikacija korisnicke podrske");
        pdq.SetTUnit("Min"); //minuta kao vremenska jedinica

        // Za svaki posluzitelj izgradi cvor i rep cekanja
        for (int i = 0; i < N; i++) {
            nName = "Serv " + i;
            cName = "Clnt " + i;

            // Stvaranje jednog posluzitelja koji zahtjeve posluzuje prema
            // redoslijedu prispjeca
            pdq.CreateNode(nName, Node.CEN, QDiscipline.FCFS);

            // Stvaranje ulaznog toka zadataka za svaki rep cekanja
            // U svaki od "N" repova cekanja dolazi "L/N" korisnika/min
            pdq.CreateOpen(cName, L / N);
        }

        // Povezi repove cekanja s posluziteljima i definiraj vrijeme posluzivanja
        for (int i = 0; i < N; i++) {

            nName = "Serv " + i;
            cName = "Clnt " + i;

            pdq.SetDemand(nName, cName, S);
        }

        // Pokretanje izracuna
        pdq.Solve(Methods.CANON);

        // Prikaz rezultata
        pdq.Report();

        //Prikaz samo W
        //System.out.println(pdq.GetResidenceTime("Serv 1", "Clnt 1", defs.TERM) - S);
    }
}
