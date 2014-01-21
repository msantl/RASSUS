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
public class Example2 {

    public static void main(String[] args) {
        PDQ pdq = new PDQ();

        final float L = 50f;    // Ucestalost dolazaka zahtjeva u rep cekanja L = 50 z/s
        final float S = 0.01f;  // Prosjecno vrijeme posluzivanja zahtjeva S = 0.01 s/z

        // Postavljanje pocetnih postavki PDQ sustava
        pdq.Init("Diskovni podsustav");

        // Stvaranje jednog posluzitelja koji provodi operacije prema
        // redoslijedu prispjeca
        pdq.CreateNode("Posluzitelj", Node.CEN, QDiscipline.FCFS);

        // Stvaranje ulaznog toka zadataka
        pdq.CreateOpen("Operacije", L);

        // Povezivanje toka zadataka s posluziteljem i definiranje vremena posluzivanja
        pdq.SetDemand("Posluzitelj", "Operacije", S);

        // Pokretanje izracuna
        pdq.Solve(Methods.CANON);

        // Prikaz rezultata
        pdq.Report();
    }
}
