package com.alwaysrejoice.hexengine.util;

import android.util.Log;
import com.alwaysrejoice.hexengine.dto.Damage;
import com.alwaysrejoice.hexengine.dto.Position;
import com.alwaysrejoice.hexengine.dto.Unit;
import com.alwaysrejoice.hexengine.dto.World;
import com.alwaysrejoice.hexengine.play.AiTools;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScriptTools {
  private Random random = new Random();
  private World world;
  private AiTools aiTools;

  public ScriptTools(World world, AiTools aiTools) {
    this.world = world;
    this.aiTools = aiTools;
  }

  /**
   * Applies damage from the self to the target.
   * This takes into account the target defence abilities
   */
  public void applyDamage(Unit self, Unit target, Damage damage) {
    if ((self==null) || (target==null) || (damage==null)) {
      Log.d("ScriptTools", "applyDamage cannot take null arguments. self="+self+" target="+target+" damage="+damage);
      return;
    }
    double attackAmount = roll(damage);
    double defenceAmount = defendAmount(target.getDefence(), damage.getType());
    double totalAmount = attackAmount - defenceAmount;
    if (totalAmount > 0.0) {
      target.setHp(target.getHp() - totalAmount);
    }
    Log.d("ScriptTools", self.getName()+" hit "+target.getName()+" for "+totalAmount+" attack="+attackAmount+" def="+defenceAmount);
  }

  /**
   * Calculate the amount of defence applicable to this attack
   * @param defence defender's defence
   * @param attackType the type of damage being done by the attack
   * @return the rolled amount of defence to reduce
   */
  public double defendAmount(List<Damage> defence, String attackType) {
    double amount = 0;
    for (Damage dmg : defence) {
      if (attackType.equals(dmg.getType())) {
        amount += roll(dmg);
      }
    } // for
    return amount;
  }

  /**
   * Calculates a damage roll
   */
  public double roll(Damage dmg) {
    double total = 0.0;
    for (int i=0; i<dmg.getCount(); i++) {
      if (dmg.getSize() == 0) {
        continue;
      }
      double rollAmt = random.nextInt(Math.abs(dmg.getSize()))+1; // nextInt is 0 to (n-1)
      if (dmg.getSize() < 0) {
        rollAmt = -rollAmt; // negative defence!
      }
      total += rollAmt;
    }
    total += dmg.getBonus();
    return total;
  }

  /**
   * Heals HP by the amount of a damage (damage type is not used)
   */
  public void heal(Unit unit, Damage dmg) {
    double amount = roll(dmg);
    unit.setHp(unit.getHp() + amount);
    if (unit.getHp() > unit.getHpMax()) {
      unit.setHp(unit.getHpMax());
    }
    Log.d("ScriptTools", unit.getName()+" healed "+amount+" HP.");
  }

  /**
   * Logs to the standard output
   * TODO : This could be stored and made user-accessible for debugging scripts
   */
  public void log(String str) {
    Log.d("Script", str);
  }

  /**
   * @return All the units on the team
   */
  public List<Unit> getTeamUnits(String teamId) {
    return aiTools.getTeamUnits(teamId);
  }


  /**
   * Finds a list of units not on your team within the visible range of self (taking terrain into account)
   */
  public List<Unit> getVisibleOthers(Unit unit) {
    String teamId = unit.getTeamId();
    List<Unit> others = new ArrayList<>();
    List<Position> teamView = aiTools.getTeamView(teamId);
    for (Unit other : world.getUnits()) {
      if (other.getTeamId().equals(unit.getTeamId())) {
        // It's a friend
        continue;
      }
      // Someone on the team can see this unit
      if (teamView.contains(other.getPos())) {
        others.add(other);
      }
     } // for other
    return others;
  }

  /**
   * @return the unit in the list that is closest to origin
   */
  public Unit getClosest(Unit origin, List<Unit> units) {
    int minDistance = -1;
    Unit closestUnit = null;
    for (Unit unit : units) {
      int distance = origin.getPos().distanceTo(unit.getPos());
      if ((distance < minDistance) || (minDistance == -1)) {
        minDistance = distance;
        closestUnit = unit;
      }
    }
    return closestUnit;
  }

  public void attack(Unit self, Unit target) {
    // TODO : the AI has to run an ability.. which is a script...

  }


}
