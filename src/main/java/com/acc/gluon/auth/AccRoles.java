package com.acc.gluon.auth;

public interface AccRoles {

    // aces principal la sistema
    String BaseAccess = "BASE_ACCESS";

    // Programagtor
    String Developer = "DEVELOPER";

    // Operator de la DITE
    String OperatorSI = "OPERATOR_SI";

    // Diretor general, comercial, adjunct etc
    String Director = "DIRECTOR";

    // Sef de sector
    String ChiefSector = "CHIEF_SECTOR";

    // contabil obisnuit
    String AccountantGeneral = "ACCOUNTANT_GENERAL";

    // contabil pe materiale
    String AccountantMaterials = "ACCOUNTANT_MATERIALS";

    // contabil pe salariu
    String AccountantPayrol = "ACCOUNTANT_PAYROLL";

    String Economist = "ECONOMIST";

    // Achizitii
    String ProcurementOfficer = "PROCUREMENT_OFFICER";

    // Inginer pentru calcul
    String CalculationsEngineer = "CALCULATIONS_ENGINEER";

    String Auditor = "AUDITOR";

    // Cadre
    String HumanResourcesMnager = "HR_MANAGER";
}
