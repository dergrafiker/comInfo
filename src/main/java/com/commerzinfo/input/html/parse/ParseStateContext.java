package com.commerzinfo.input.html.parse;

import com.commerzinfo.DataRow;
import com.commerzinfo.input.html.parse.state.ParseState;
import com.commerzinfo.input.html.parse.state.StateBetrag;
import com.commerzinfo.input.html.parse.state.StateBuchungstag;
import com.commerzinfo.input.html.parse.state.StateBuchungstext;
import com.commerzinfo.input.html.parse.state.StateValuta;
import com.commerzinfo.util.ReflectionUtil;
import org.tinylog.Logger;


public class ParseStateContext {
    private static final ParseState BUCHUNGSTAG_STATE = new StateBuchungstag();
    public static final ParseState INITIAL_STATE = BUCHUNGSTAG_STATE;
    private ParseState state = INITIAL_STATE;
    public static final ParseState BUCHUNGSTEXT_STATE = new StateBuchungstext();
    public static final ParseState VALUTA_STATE = new StateValuta();
    public static final ParseState BETRAG_STATE = new StateBetrag();
    private DataRow buchungszeile;

    public ParseStateContext() {
        Logger.trace("initialized " + this.getClass().getSimpleName());
        setState(INITIAL_STATE);
        buchungszeile = new DataRow();
    }

    public void resetToInitialState() {
        Logger.trace("resetToInitialState. old " + this.state.getStateName() + " => " +
                INITIAL_STATE.getStateName());

        setState(INITIAL_STATE);
        buchungszeile = new DataRow();
    }

    public void setState(ParseState state) {
        String oldState = this.state != null ? this.state.getStateName() : "";
        Logger.trace("changeState " + oldState + " => " + state
                .getStateName());

        this.state = state;
    }

    public DataRow getBuchungszeile() {
        return buchungszeile;
    }

    public DataRow processInput(ParseStateContext stateContext, String input) {
        Logger.trace("input: " + input);

        state.processInput(stateContext, input);

        if (ReflectionUtil.allFieldsFilled(this.buchungszeile)) {
            Logger.trace("found all required fields. returning a new row " + this.buchungszeile.toString());

            DataRow returnValue = this.buchungszeile; //save the reference - it is set to new in resetToInitialState()
            resetToInitialState();
            return returnValue;
        } else {
            return null;
        }
    }
}
