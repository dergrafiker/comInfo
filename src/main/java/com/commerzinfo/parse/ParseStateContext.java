package com.commerzinfo.parse;

import com.commerzinfo.data.DataRow;
import com.commerzinfo.parse.state.ParseState;
import com.commerzinfo.parse.state.StateBetrag;
import com.commerzinfo.parse.state.StateBuchungstag;
import com.commerzinfo.parse.state.StateBuchungstext;
import com.commerzinfo.parse.state.StateValuta;
import com.commerzinfo.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseStateContext {
    private static Logger logger = LoggerFactory.getLogger(ParseStateContext.class);

    public static final ParseState BUCHUNGSTAG_STATE = new StateBuchungstag();
    public static final ParseState BUCHUNGSTEXT_STATE = new StateBuchungstext();
    public static final ParseState VALUTA_STATE = new StateValuta();
    public static final ParseState BETRAG_STATE = new StateBetrag();
    public static final ParseState INITIAL_STATE = BUCHUNGSTAG_STATE;

    private ParseState state = INITIAL_STATE;
    private DataRow buchungszeile;

    public ParseStateContext() {
        if (logger.isTraceEnabled()) {
            logger.trace("initialized " + this.getClass().getSimpleName());
        }
        setState(INITIAL_STATE);
        buchungszeile = new DataRow();
    }

    public void resetToInitialState() {
        if (logger.isTraceEnabled()) {
            logger.trace("resetToInitialState. old " + this.state.getStateName() + " => " +
                    INITIAL_STATE.getStateName());
        }
        setState(INITIAL_STATE);
        buchungszeile = new DataRow();
    }

    public void setState(ParseState state) {
        if (logger.isTraceEnabled()) {
            String oldState = this.state != null ? this.state.getStateName() : "";
            logger.trace("changeState " + oldState + " => " + state
                    .getStateName());
        }
        this.state = state;
    }

    public DataRow getBuchungszeile() {
        return buchungszeile;
    }

    public DataRow processInput(ParseStateContext stateContext, String input) {
        if (logger.isTraceEnabled()) {
            logger.trace("input: " + input);
        }
        state.processInput(stateContext, input);

        if (ReflectionUtil.allFieldsFilled(this.buchungszeile)) {
            if (logger.isTraceEnabled()) {
                logger.trace("found all required fields. returning a new row " + this.buchungszeile.toString());
            }

            DataRow returnValue = this.buchungszeile; //save the reference - it is set to new in resetToInitialState()
            resetToInitialState();
            return returnValue;
        } else {
            return null;
        }
    }
}
