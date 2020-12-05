package mcts.montecarlo;

import mcts.tree.Node;

public class UCT {
    public static double uctValue(int totalVisit, double nodeWinScore, int nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        return (nodeWinScore / (double) nodeVisit) + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    static Node findBestNodeWithUCT(Node node) {
        double maxValue = Double.MIN_VALUE;
        Node bestNode = null;
        for(Node childNode : node.getChildArray()){
            double uctValue = uctValue(node.getState().getVisitCount(), childNode.getState().getWinScore(), childNode.getState().getVisitCount());
            if(uctValue > maxValue){
                maxValue = uctValue;
                bestNode = childNode;
            }
        }
        return bestNode;
    }
}
