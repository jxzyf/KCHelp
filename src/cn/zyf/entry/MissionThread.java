package cn.zyf.entry;

import cn.zyf.config.Config;
import cn.zyf.model.Ship;
import cn.zyf.model.ShipAttribute;
import cn.zyf.operation.*;
import cn.zyf.resource.Resource;
import cn.zyf.util.Delay;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class MissionThread extends Thread {

    private static final Logger logger = LogManager.getLogger(MissionThread.class);

    private int deckId;

    public MissionThread(int deckId) {
        this.deckId = deckId;
    }

    @Override
    public void run() {
        outer: while (true) {
            if (Thread.currentThread().isInterrupted()) {
                logger.info(Thread.currentThread().getName() + " is interrupted, will return");
                return;
            }
            long complatetime = checkMissionStateAndSend();
            while (System.currentTimeMillis() < complatetime) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    continue outer;
                }
            }
            synchronized (MissionThread.class) {
                Port.port();
                Delay.random();
                Result.result(this.deckId);
                Port.port();
                UseItem.useitem();
                Delay.random();
            }
            Resource.setProperty("mission_" + this.deckId + "_count",
                    null == Resource.getProperty("mission_" + this.deckId + "_count") ? 1 : (int) Resource.getProperty(
                            "mission_" + this.deckId + "_count") + 1);
            Delay.sleep(3000);
            synchronized (MissionThread.class) {
                charge();
                Delay.random();
            }
            while (Config.stop) {
                // 暂停远征
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    continue outer;
                }
            }
            synchronized (MissionThread.class) {
                Port.port();
                Delay.random();
                MissionGet.get();
                Delay.random();
                MissionStart.start(this.deckId, (int) Resource.getProperty("deck" + this.deckId + "_mission"));
                Delay.random();
                Port.port();
                Delay.random();
            }
        }
    }

    private long checkMissionStateAndSend() {
        Map deckInfo = (Map) ((Map) Resource.getProperty("decks")).get("deck_" + this.deckId);
        long complatetime = (long) deckInfo.get("api_complatetime");
        if (0 == complatetime) {
            // 未开始远征
            if (needCharge(deckInfo)) {
                charge();
                Delay.random();
            }
            Port.port();
            Delay.random();
            MissionGet.get();
            Delay.random();
            MissionStart.start(this.deckId, (int) Resource.getProperty("deck" + this.deckId + "_mission"));
            Delay.random();
        }
        return (long) ((Map) ((Map) Resource.getProperty("decks")).get("deck_" + this.deckId)).get("api_complatetime");
    }

    private boolean needCharge(Map deckInfo) {
        List<Integer> shipGetId = (List<Integer>) deckInfo.get("api_ship");
        Map<Integer, ShipAttribute> shipAttributes = (Map<Integer, ShipAttribute>) Resource.getProperty("ship_attribute");
        Map<Integer, Ship> ships = (Map<Integer, Ship>) Resource.getProperty("ships");

        boolean need = false;
        for (Integer getId : shipGetId) {
            Ship ship = ships.get(getId);
            Integer shipId = ship.getApi_ship_id();
            ShipAttribute shipAttribute = shipAttributes.get(shipId);
            if (ship.getApi_fuel() < shipAttribute.getApi_fuel_max() || ship.getApi_bull() < shipAttribute.getApi_bull_max()) {
                logger.info(String.format("%s needs charge: [fuel]%d->%d, [bull]%d->%d",
                        shipAttribute.getApi_name(),
                        ship.getApi_fuel(),
                        shipAttribute.getApi_fuel_max(),
                        ship.getApi_bull(),
                        shipAttribute.getApi_bull_max()));
                need = true;
            }
        }
        return need;
    }

    private void charge() {
        Charge.charge(this.deckId);
    }

}
