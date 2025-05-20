package com.example.fertig.gameState;

import com.example.fertig.structure.GewinnerCheck;

public interface ClassicComputerInterface {
    int maxRecursionDepth = 10;

    int computerZug(GameState state);
}
