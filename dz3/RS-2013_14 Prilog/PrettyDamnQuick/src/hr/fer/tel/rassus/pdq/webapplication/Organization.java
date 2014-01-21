/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.fer.tel.rassus.pdq.webapplication;

import com.perfdynamics.pdq.Job;
import com.perfdynamics.pdq.Methods;
import com.perfdynamics.pdq.Node;
import com.perfdynamics.pdq.PDQ;
import com.perfdynamics.pdq.QDiscipline;

/**
 *
 * @author Kreso
 */
public class Organization {

    public static void main(String[] args) {
        PDQ pdq = new PDQ();

        final int N_AP = 1;
        final int N_BP = 10;
        final int N_IM = 1;

        final float p_IM = 0.1f;
        final float p_BP = 0.15f;

        final float S_ZZ = 0.001f;
        final float S_PO = 0.001f;

        final float S_AP = 0.3f;
        final float S_BP = 2.5f/N_BP;
        final float S_IM = 0.5f;
       
        final float L_inc = 0.1f;
        final float L_max = 2.0f;

        System.out.print("L\t");

        for (int i = 0; i < N_AP; i++) {
            System.out.print("AP" + i + "\t");
        }

        for (int i = 0; i < N_BP; i++) {
            System.out.print("BP" + i + "\t");
        }

        for (int i = 0; i < N_IM; i++) {
            System.out.print("IM" + i + "\t");
        }
        System.out.print("ZZ\t");
        System.out.print("PO\t");
        System.out.println("R");

        for (float L = L_inc; L < L_max + L_inc; L += L_inc) {
            pdq.Init("Web aplikacija");
            pdq.CreateOpen("Zahtjevi", L);

            pdq.CreateNode("ZZ", Node.CEN, QDiscipline.FCFS);
            pdq.CreateNode("PO", Node.CEN, QDiscipline.FCFS);

            for (int i = 0; i < N_AP; i++) {
                pdq.CreateNode("AP" + i, Node.CEN, QDiscipline.FCFS);
            }

            for (int i = 0; i < N_BP; i++) {
                pdq.CreateNode("BP" + i, Node.CEN, QDiscipline.FCFS);
            }

            for (int i = 0; i < N_IM; i++) {
                pdq.CreateNode("IM" + i, Node.CEN, QDiscipline.FCFS);
            }

            pdq.SetVisits("ZZ", "Zahtjevi", 1.0f, S_ZZ);
            pdq.SetVisits("PO", "Zahtjevi", 1.0f, S_PO);

            for (int i = 0; i < N_AP; i++) {
                pdq.SetVisits("AP" + i, "Zahtjevi", ((1 - p_IM) / (1 - p_BP)) / N_AP, S_AP);
            }

            for (int i = 0; i < N_BP; i++) {
                pdq.SetVisits("BP" + i, "Zahtjevi", (p_BP * (1 - p_IM) / (1 - p_BP)) / N_BP, S_BP);
            }

            for (int i = 0; i < N_IM; i++) {
                pdq.SetVisits("IM" + i, "Zahtjevi", p_IM / N_IM, S_IM);
            }

            pdq.Solve(Methods.CANON);            

            System.out.print(L + "\t");

            for (int i = 0; i < N_AP; i++) {
                System.out.print(pdq.GetResidenceTime("AP" + i, "Zahtjevi", Job.TRANS) + "\t");
            }

            for (int i = 0; i < N_BP; i++) {
                System.out.print(pdq.GetResidenceTime("BP" + i, "Zahtjevi", Job.TRANS) + "\t");
            }

            for (int i = 0; i < N_IM; i++) {
                System.out.print(pdq.GetResidenceTime("IM" + i, "Zahtjevi", Job.TRANS) + "\t");
            }

            System.out.print(pdq.GetResidenceTime("ZZ", "Zahtjevi", Job.TRANS) + "\t");

            System.out.print(pdq.GetResidenceTime("PO", "Zahtjevi", Job.TRANS) + "\t");

            System.out.println(pdq.GetResponse(Job.TRANS, "Zahtjevi"));
        }
    }
}
