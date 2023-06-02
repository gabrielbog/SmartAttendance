package com.gabrielbog.smartattendance.models.responses;

import com.gabrielbog.smartattendance.models.ProfessorGrups;

import java.io.Serializable;
import java.util.List;

public class ProfessorGrupsResponse implements Serializable {
    private int code;
    private List<ProfessorGrups> professorGrupsList;

    public ProfessorGrupsResponse(int code, List<ProfessorGrups> professorGrupsList) {
        this.code = code;
        this.professorGrupsList = professorGrupsList;
    }

    public int getCode() {
        return code;
    }

    public List<ProfessorGrups> getProfessorGrupsList() {
        return professorGrupsList;
    }
}
