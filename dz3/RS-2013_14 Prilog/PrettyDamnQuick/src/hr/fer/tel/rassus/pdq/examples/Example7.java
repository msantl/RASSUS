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
public class Example7 {

    public static void main(String[] args) {
        PDQ pdq = new PDQ();

        final float p_err = 0.30f; // vjerojatnost retransmisije
        final float L = 0.50f; // Ucestalost dolazaka zahtjeva u rep cekanja L = 0.50 z/s
        final float S = 0.75f; // Prosjecno vrijeme posluzivanja zahtjeva S = 0.75 s/z

        // Odredivanje faktora prosjecnog broja posjeta
        float V = 1.0f / (1.0f - p_err);

        // Postavljanje pocetnih postavki PDQ sustava
        pdq.Init("Posluzitelj s repom i povratnom vezom");

        // Stvaranje jednog posluzitelja koji zahtjeve posluzuje prema
        // redoslijedu prispjeca
        pdq.CreateNode("Kanal", Node.CEN, QDiscipline.FCFS);

        // Stvaranje ulaznog toka zadataka
        pdq.CreateOpen("Poruka", L);

        // Povezivanje toka zadataka s posluziteljem s definiranje prosjecnog broja posjeta i vremena posluzivanja
        pdq.SetVisits("Kanal", "Poruka", V, S);

        // Pokretanje izracuna
        pdq.Solve(Methods.CANON);

        // Prikaz rezultata
        pdq.Report();
    }
}
