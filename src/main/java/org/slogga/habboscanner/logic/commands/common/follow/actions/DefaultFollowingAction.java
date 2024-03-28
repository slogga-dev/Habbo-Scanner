package org.slogga.habboscanner.logic.commands.common.follow.actions;

import gearth.protocol.HMessage;

import lombok.*;

import org.slogga.habboscanner.logic.game.furni.FurniInsightsAndTransactionExecutor;

@EqualsAndHashCode(callSuper = true)
@Data
public class DefaultFollowingAction extends BaseFollowingAction {
    private final FurniInsightsAndTransactionExecutor furniInsightsAndTransactionExecutor = new FurniInsightsAndTransactionExecutor();

    @Override
    public void execute(HMessage message) {
        super.execute(message);

        furniInsightsAndTransactionExecutor.executeTransactionsAndProvideFurniInsights(super.estimatedDate);
    }
}
