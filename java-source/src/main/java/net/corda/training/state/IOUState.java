package net.corda.training.state;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.core.serialization.CordaSerializable;
import net.corda.finance.Currencies;
import org.jetbrains.annotations.NotNull;

import java.util.Currency;
import java.util.List;

/**
 * This is where you'll add the definition of your state object. Look at the unit tests in [IOUStateTests] for
 * instructions on how to complete the [IOUState] class.
 */
@BelongsToContract(net.corda.training.contract.IOUContract.class)
@CordaSerializable
public class IOUState implements ContractState, LinearState {

    final private Amount<Currency> amount;

    final private Party lender;
    final private Party borrower;

    final private Amount<Currency> paid;
    final private UniqueIdentifier linearId;

    private IOUState(
            final Amount<Currency> amount,
            final Party lender,
            final Party borrower,
            final Amount<Currency> paid,
            final UniqueIdentifier linearId
    ) {
        this.amount = amount;
        this.lender = lender;
        this.borrower = borrower;
        this.paid = paid;
        this.linearId = linearId;
    }

    @ConstructorForDeserialization
    public IOUState(final Amount<Currency> amount, final Party lender, final Party borrower) {
        this.amount = amount;
        this.lender = lender;
        this.borrower = borrower;
        this.paid = Currencies.DOLLARS(0);
        this.linearId = new UniqueIdentifier(this.getClass().toString());
    }

    /**
     * This method will return a list of the nodes which can "use" this state in a valid transaction. In this case, the
     * lender or the borrower.
     */
    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(lender, borrower);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    public Amount<Currency> getPaid() {
        return this.paid;
    }

    public Party getLender() {
        return lender;
    }

    public Party getBorrower() {
        return borrower;
    }

    public Amount<Currency> getAmount() {
        return amount;
    }

    public IOUState pay(Amount<Currency> amount) {
        return new IOUState(this.amount, this.lender, this.borrower, this.getPaid().plus(amount), this.linearId);
    }

    public IOUState withNewLender(final Party lender) {
        return new IOUState(this.amount, lender, this.borrower, this.paid, this.linearId);
    }
}