/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.fer.tel.rassus.pdq.examples;

import com.perfdynamics.pdq.Job;
import com.perfdynamics.pdq.Methods;
import com.perfdynamics.pdq.Node;
import com.perfdynamics.pdq.PDQ;
import com.perfdynamics.pdq.QDiscipline;

/**
 *
 * @author Kreso
 */
public class Example8 {

    public static void main(String[] args) {
        PDQ pdq = new PDQ();

        final int m = 230;       //broj aktivnih razvojnih inženjera
        final int Z = 300;       //prosječno vrijeme između kompilacija
        final float S = 0.63f;   //prosječno vrijeme kompilacije

        // Postavljanje pocetnih postavki PDQ sustava
        pdq.Init("multibus");
             
        // Stvaranje jednog posluzitelja koji zahtjeve posluzuje prema
        // redoslijedu prispjeca
        pdq.CreateNode("bus", Node.CEN, QDiscipline.FCFS);

        // Stvaranje zatvorenog ulaznog toka zahtjeva
        // Broj generatora zahtjeva je m, a Z je vrijeme postavljanja zahtjeva (razmišljanja)
        pdq.CreateClosed("reqs", Job.TERM, m, Z);

        //Definiranje vremena posluživanja za zahtjeve na poslužitelju
        pdq.SetDemand("bus", "reqs", S);

        // Pokretanje izracuna
        pdq.Solve(Methods.EXACT);

        // Prikaz rezultata
        pdq.Report();
    }
}
