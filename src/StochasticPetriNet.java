import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.TransientSolutionViewer;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.oristool.models.stpn.*;

public class StochasticPetriNet {
    PetriNet pn = new PetriNet();

    StochasticPetriNet() {
        Place ready = pn.addPlace("Ready");
        Place arrival = pn.addPlace("Arrival");
        Transition genEvent = pn.addTransition("gen_event");

        pn.addPrecondition(ready, genEvent);
        pn.addPostcondition(genEvent, arrival);
        pn.addPostcondition(genEvent, ready);

        // 2. Aggiunta della distribuzione esponenziale alla transizione
        genEvent.addFeature(StochasticTransitionFeature.newExponentialInstance("1 + sin(2 * pi * t)"));
    }

    public void simulate(double maxTime, double step, double error) {
        Marking initial = new Marking();
        initial.addTokens(pn.getPlace("Ready"), 1);

        // 7. Simulazione transiente
        RegTransient analysis = RegTransient.builder()
                .greedyPolicy(new BigDecimal(maxTime), new BigDecimal(error))
                .timeStep(new BigDecimal(step))
                .build();


        // 5. Esegui l'analisi
        TransientSolution<DeterministicEnablingState, Marking> solution = analysis.compute(pn, initial);
        // display transient probabilities
        solution.writeCSV("output.csv", 6);
    }
}