package org.slogga.habboscanner.logic.game.commands.common.follow.actions;

import gearth.extensions.parsers.HFloorItem;
import gearth.protocol.HMessage;
import lombok.Data;
import org.slogga.habboscanner.logic.game.furni.FurniInsightsAndTransactionExecutor;
@Data
public class DefaultFollowingAction extends BaseFollowingAction {
    private final FurniInsightsAndTransactionExecutor furniInsightsAndTransactionExecutor = new FurniInsightsAndTransactionExecutor();

    @Override
    public void execute(HMessage message) {
        super.execute(message);

        furniInsightsAndTransactionExecutor.executeTransactionsAndProvideFurniInsights(super.estimatedDate);
    }
}
