package com.alwaysrejoice.hexengine.util;

import android.util.Log;

import com.alwaysrejoice.hexengine.dto.Damage;
import com.alwaysrejoice.hexengine.dto.Unit;
import com.alwaysrejoice.hexengine.dto.World;

import java.util.List;
import java.util.Random;

public class ScriptTools {
  private Random random = new Random();
  private World world;

  public ScriptTools(World world) {
    this.world = world;
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

}
