package net.corda.training.contract;

import net.corda.core.contracts.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.finance.Currencies;
import net.corda.training.state.IOUState;

import java.util.HashSet;
import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * This is where you'll add the contract code which defines how the [IOUState] behaves. Looks at the unit tests in
 * [IOUContractTests] for instructions on how to complete the [IOUContract] class.
 */
public class IOUContract implements Contract {
    public static final String IOU_CONTRACT_ID = "net.corda.training.contract.IOUContract";

    /**
     * The contract code for the [IOUContract].
     * The constraints are self documenting so don't require any additional explanation.
     */
    @Override
    public void verify(LedgerTransaction tx) {
        // Add contract code here.
        // requireThat(req -> {
        //     ...
        // });
        // Input transaction must be of type issue command
        CommandWithParties<Commands.Issue> command = requireSingleCommand(tx.getCommands(), Commands.Issue.class);

        requireThat(requirement -> {
            // There should not be any input state for issue command
            requirement.using("No inputs should be consumed when issuing an IOU", tx.getInputStates().isEmpty());
            requirement.using("Only one output state should be created when issuing an IOU.", tx.getOutputStates().size() == 1);

            IOUState outputState = (IOUState) tx.getOutputStates().get(0);
            requirement.using("A newly issued IOU must have a positive amount.", outputState.getAmount().getQuantity() > 0);

            requirement.using("The lender and borrower cannot have the same identity.", !outputState.getLender().equals(outputState.getBorrower()));
            
            // Both lender and borrower must sign the transaction
            requirement.using("Both lender and borrower together only may sign IOU issue transaction.", new HashSet<>(outputState.getParticipants()).stream().map(AbstractParty::getOwningKey).collect(Collectors.toSet()).equals(new HashSet<>(command.getSigners())));
            return null;
        });
    }

    /**
     * Add any commands required for this contract as classes within this interface.
     * It is useful to encapsulate your commands inside an interface, so you can use the [requireSingleCommand]
     * function to check for a number of commands which implement this interface.
     */
    public interface Commands extends CommandData {
        // Add commands here.
        // E.g
        // class DoSomething extends TypeOnlyCommandData implements Commands{}
        class Issue extends TypeOnlyCommandData implements Commands {
        }
    }
}