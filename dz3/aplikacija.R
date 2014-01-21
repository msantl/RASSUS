library(pdq)

# udio ulaza koji odlazi na ap1
p_ap1 = 0.2
# udio ulaza koji odlazi na ap2
p_ap2 = 0.3
# udio ulaza koji odlazi na ap3
p_ap3 = 0.5

# udio izlaza ap1 koji odlazi na bp1
p1_bp1 = 0.1
# udio izlaza ap1 koji odlazi na bp2
p1_bp2 = 0.3

# udio izlaza ap2 koji odlazi na bp1
p2_bp1 = 0.3
# udio izlaza ap2 koji odlazi na bp2
p2_bp2 = 0.1

# udio izlaza ap3 koji odlazi na bp2
p3_bp2 = 0.1

# ulaz
S_ZZ = 0.001
# izlaz
S_PO = 0.001

# svi aplikacijski grozdovi su jednaki
S_AP = 0.3
# sve baze podataka su jednake
S_BP = 4.5

# apsolutne vrijednosti udjela
v_ap1 = p_ap1 / (1 - p1_bp1 - p1_bp2)
v_ap2 = p_ap2 / (1 - p2_bp1 - p2_bp2)
v_ap3 = p_ap3 / (1 - p3_bp2)

v_bp1 = p1_bp1 * v_ap1 + p2_bp1 * v_ap2
v_bp2 = p1_bp2 * v_ap1 + p2_bp2 * v_ap2 + p3_bp2 * v_ap3

# print(c(v_ap1, v_ap2, v_ap3, v_bp1, v_bp2))

# domena
L_inc = 0.04
L_start = L_inc
L_end = 1.08

# solve for various L's 
for (L in seq(L_start, L_end, by=L_inc)) {
	Init("RASSUS - 3.dz")
	
	CreateOpen("Zahtjevi", L)
	
	SetWUnit("Zahtjevi")
	SetTUnit("Sec")
	
	# create nodes
	CreateNode("ZZ", CEN, FCFS)
	CreateNode("PO", CEN, FCFS)
	
	CreateNode("AP1", CEN, FCFS)
	CreateNode("AP2", CEN, FCFS)
	CreateNode("AP3", CEN, FCFS)
	
	CreateNode("BP1", CEN, FCFS)
	CreateNode("BP2", CEN, FCFS)
	
	# connect them
	SetVisits("ZZ", "Zahtjevi", 1.0, S_ZZ)
	SetVisits("PO", "Zahtjevi", 1.0, S_PO)
	
	SetVisits("AP1", "Zahtjevi", v_ap1, S_AP)
	SetVisits("AP2", "Zahtjevi", v_ap2, S_AP)
	SetVisits("AP3", "Zahtjevi", v_ap3, S_AP)	
	
	SetVisits("BP1", "Zahtjevi", v_bp1, S_BP)
	SetVisits("BP2", "Zahtjevi", v_bp2, S_BP)

	# pdq magic
	Solve(CANON)	
	
	# prepare results
	response = sprintf("%.3f", 
		GetResponse(TRANS, "Zahtjevi"))
	
	t_ap1 = sprintf("%.3f", 
		GetResidenceTime("AP1", "Zahtjevi", TRANS))
	t_ap2 = sprintf("%.3f", 
		GetResidenceTime("AP2", "Zahtjevi", TRANS))
	t_ap3 = sprintf("%.3f", 
		GetResidenceTime("AP3", "Zahtjevi", TRANS))
	
	t_bp1 = sprintf("%.3f", 
		GetResidenceTime("BP1", "Zahtjevi", TRANS))
	t_bp2 = sprintf("%.3f", 
		GetResidenceTime("BP2", "Zahtjevi", TRANS))
	
	t_zz = sprintf("%.3f", 
		GetResidenceTime("ZZ", "Zahtjevi", TRANS))
	t_po = sprintf("%.3f", 
		GetResidenceTime("PO", "Zahtjevi", TRANS))
	
	l = sprintf("%.3f", L)
	
	result <- c(L, t_ap1, t_ap2, t_ap3, 
				t_bp1, t_bp2, t_zz, t_po, response)
	
	print(result)
	
	# Report()
}