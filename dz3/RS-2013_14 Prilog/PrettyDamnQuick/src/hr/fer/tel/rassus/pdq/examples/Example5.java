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
public class Example5 {

    public static void main(String[] args) {
        PDQ pdq = new PDQ();

        final float L = 0.1f; // Ucestalost dolazaka zahtjeva u rep cekanja L = 0.1 z/s
        final float S1 = 1.0f; // Prosjecno vrijeme posluzivanja zahtjeva S1 = 1.0 s/z
        final float S2 = 2.0f; // Prosjecno vrijeme posluzivanja zahtjeva S2 = 2.0 s/z
        final float S3 = 3.0f; // Prosjecno vrijeme posluzivanja zahtjeva S3 = 3.0 s/z

        // Postavljanje pocetnih postavki PDQ sustava
        pdq.Init("Serija tri posluzitelja");       

        // Stvaranje ulaznog toka zadataka
        pdq.CreateOpen("Zadaci", L);

        // Stvaranje tri posluzitelja koji zahtjeve posluzuju prema
        // redoslijedu prispjeca
        pdq.CreateNode("Posluzitelj1", Node.CEN, QDiscipline.FCFS);
        pdq.CreateNode("Posluzitelj2", Node.CEN, QDiscipline.FCFS);
        pdq.CreateNode("Posluzitelj3", Node.CEN, QDiscipline.FCFS);

        // Povezivanje toka zadataka s posluziteljem
        pdq.SetDemand("Posluzitelj1", "Zadaci", S1);
        pdq.SetDemand("Posluzitelj2", "Zadaci", S2);
        pdq.SetDemand("Posluzitelj3", "Zadaci", S3);

        // Pokretanje izracuna
        pdq.Solve(Methods.CANON);

        // Prikaz rezultata
        pdq.Report();
    }
}
