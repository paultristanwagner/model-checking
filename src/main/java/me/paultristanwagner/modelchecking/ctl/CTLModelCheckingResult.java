package me.paultristanwagner.modelchecking.ctl;

import me.paultristanwagner.modelchecking.ModelCheckingResult;

public class CTLModelCheckingResult extends ModelCheckingResult {

    public CTLModelCheckingResult( boolean models ) {
        super( models );
    }

    public static CTLModelCheckingResult models() {
        return new CTLModelCheckingResult( true );
    }

    public static CTLModelCheckingResult doesNotModel() {
        return new CTLModelCheckingResult( false );
    }
}
