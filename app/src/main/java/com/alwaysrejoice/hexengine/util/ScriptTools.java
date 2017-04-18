package com.alwaysrejoice.hexengine.util;

import android.util.Log;
import com.alwaysrejoice.hexengine.dto.Ability;
import com.alwaysrejoice.hexengine.dto.BgMap;
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.Damage;
import com.alwaysrejoice.hexengine.dto.Effect;
import com.alwaysrejoice.hexengine.dto.Position;
import com.alwaysrejoice.hexengine.dto.Unit;
import com.alwaysrejoice.hexengine.dto.World;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ScriptTools {
  private Random random = new Random();
  private World world;
  private ScriptEngine scriptEngine;


  public ScriptTools(ScriptEngine scriptEngine) {
    this.scriptEngine = scriptEngine;
    world = WorldUtils.getWorld();
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
   */
  public void log(String str) {
   // Feature: This could be stored and made user-accessible for debugging scripts
    Log.d("Script", str);
  }

  /**
   * Finds a list of units not on your team within the visible range of self (taking terrain into account)
   */
  public List<Unit> getVisibleOthers(Unit unit) {
    if (unit == null) {
      return null;
    }
    String teamId = unit.getTeamId();
    List<Unit> others = new ArrayList<>();
    List<Position> teamView = getTeamView(teamId);
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
   * ALl units that are not on your team
   */
  public List<Unit> getAllOthers(String teamId) {
    List<Unit> others = new ArrayList<>();
    for (Unit other : world.getUnits()) {
      if (!other.getTeamId().equals(teamId)) {
        others.add(other);
      }
    }
    return others;
  }

  /**
   * @return the unit in the list that is closest to origin
   */
  public Unit getClosest(Unit origin, List<Unit> units) {
    if ((origin == null) || (units == null)) {
      return null;
    }
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

  /**
   * Finds the closest visible unit not on the same team
   * // Null if none
   */
  public Unit getClosestVisibleEnemy(Unit self) {
    return getClosest(self, getVisibleOthers(self));
  }

  /**
   * @return all the positions visible to any unit on the team
   */
  public List<Position> getTeamView(String teamId) {
    List<Position> teamView = new ArrayList<>();
    // Using a set to remove duplicates
    Set<Position> teamViewSet = new HashSet<>();
    // Add together the views of every member on the team
    for (Unit unit : getTeamUnits(teamId)) {
      teamViewSet.addAll(getVisiblePositions(unit));
    }
    teamView.addAll(teamViewSet);
    return teamView;
  }

  /**
   * @return All the units on the specified team
   */
  public List<Unit> getTeamUnits(String teamId) {
    List<Unit> teamUnits = new ArrayList<>();
    for (Unit unit : world.getUnits()) {
      if (teamId.equals(unit.getTeamId())) {
        teamUnits.add(unit);
      }
    } // for
    return teamUnits;
  }

  /**
   * Lookup all the valid positions given the restrictions
   * @return a list of all the valid positions
   */
  public List<Position> validMovePositions(Position origin, int range, List<String> restrict) {
    List<Position> validPositions = new ArrayList<>();
    // nStepsAway[n] contains all the positions that are n steps away from origin
    List<List<Position>> fringes = new ArrayList<>(range);
    List<Position> root = new ArrayList<>();
    root.add(origin);
    fringes.add(root); // index 0

    for (int n=1; n<=range; n++) {
      List<Position> fringe = new ArrayList<>();
      fringes.add(fringe); // index n
      // Go through all the previous step's fringe
      for (Position pos : fringes.get(n-1)) {
        for (Position neighbor : pos.getNeighbors()) {
          if (!validPositions.contains(neighbor) && isValidPosition(neighbor, restrict, true)) {
            validPositions.add(neighbor);
            fringe.add(neighbor);
          }
        }  // for neighbor
      } // for pos
    } // for n
    return validPositions;
  }


  /**
   * Looks up the bgTile at the specified position and returns true if the tile
   * is a valid position given the terrain type restrictions
   * @return true if the position is valid, false if not or if there was an error
   * @param onlyUnoccupied Add a check to ensure the tile is not occupied by a unit
   */
  public boolean isValidPosition(Position pos, List<String> restrict, boolean onlyUnoccupied) {
    String bgTileId = null;
    for (BgMap bgMap : world.getBgMaps()) {
      if ((bgMap.getRow() == pos.getRow()) && (bgMap.getCol() == pos.getCol())) {
        bgTileId = bgMap.getBgTileId();
      }
    } // for
    if (bgTileId == null) {
      return false;
    }
    BgTile bgTile = world.getBgTiles().get(bgTileId);
    if (bgTile == null) {
      Log.d("GameUtils", "Error! Unable to find bgTileId="+bgTileId+" in the list of tiles. The game file is corrupt.");
      return false;
    }
    // You are not allowed to move to that kind of terrain
    if (restrict.contains(bgTile.getType())) {
      return false;
    }
    if (onlyUnoccupied) {
      // Already a unit at that location - you can't move there
      for (Unit unit : world.getUnits()) {
        if (pos.equals(unit.getPos())) {
          return false;
        }
      } // for
    }
    return true;
  }

  /**
   * @return a list of all the positions on the map that are not restricted
   * @param restrictions List of all the terrain types that are blocking/restricted
   */
  public Map<Position, BgTile> unrestrictedPositions(List<String> restrictions, boolean onlyUnoccupied) {
    Map<Position, BgTile> unrestrictedPositions = new HashMap<>();
    for (BgMap bgMap : world.getBgMaps()) {
      String bgTileId = bgMap.getBgTileId();
      if (bgTileId == null) {
        continue;
      }
      BgTile bgTile = world.getBgTiles().get(bgTileId);
      if (bgTile == null) {
        Log.d("GameUtils", "Error! Unable to find bgTileId="+bgTileId+" in the list of tiles. The game file is corrupt.");
        continue;
      }
      // If it's a non-restricted terrain, we are good to go
      if (!restrictions.contains(bgTile.getType())) {
        boolean validPosition = true;
        // Optional occupied check (quite expensive)
        if (onlyUnoccupied) {
          // Already a unit at that location - you can't move there
          for (Unit unit : world.getUnits()) {
            if (bgMap.getPos().equals(unit.getPos())) {
              validPosition = false;
              break; // for unit
            }
          } // for unit
        }
        if (validPosition) {
          unrestrictedPositions.put(bgMap.getPos(), bgTile);
        }
      }
    } // for bgMap
    return unrestrictedPositions;
  }

  /**
   * Finds the first (and only) unit at the selected position. If no unit is there, this will return null
   */
  public Unit getUnitAt(Position pos) {
    for (Unit unit : world.getUnits()) {
      if (unit.getPos().equals(pos)) {
        return unit;
      }
    }// for
    return null;
  }

  /**
   * @return true if position a and b are close enough to be in range, and nothing in between of sighRestrict is blocking
   */
  public boolean hasLineOfSight(Position a, Position b, int sightRange, List<String> sightRestrict) {
    if ((a == null) || (b == null)) {
      return false;
    }
    // You can always see things one space away
    if (sightRange <= 1) {
      return true;
    }
    if (a.distanceTo(b) > sightRange) {
      return false;
    }
    List<Position> line = a.lineTo(b);
    boolean blocked = false;
    for (Position linePos : line) {
      if (!isValidPosition(linePos, sightRestrict, false)) {
        blocked = true;
        break;
      }
    } // for linePos
    return !blocked;
  }


  /**
   * Returns a list of positions that are visible to the unit
   * This works on a line of sight basis using the unit's sightRange
   * and is blocked by the units sightRestrict
   */
  public List<Position> getVisiblePositions(Unit unit) {
    List<Position> visible = new ArrayList<>();
    Position selfPos = unit.getPos();
    List<Position> allPossible = selfPos.posInRange(unit.getSightRange());
    // If the unit can see through all terrain, we don't need to to do the rather expensive check
    if (unit.getSightRestrict().size()==0) {
      return allPossible;
    }
    for (Position pos : allPossible) {
      List<Position> line = selfPos.lineTo(pos);
      boolean blocked = false;
      for (Position linePos : line) {
        if (!isValidPosition(linePos, unit.getSightRestrict(), false)) {
          blocked = true;
          break;
        }
      } // for linePos
      //Log.d("ScriptTools", "visiblePositions blocked="+blocked+" pos="+pos+" restrict="+self.getSightRestrict());
      if (!blocked) {
        visible.add(pos);
      }
    } // for pos
    return visible;
  }

  /**
   * Checks all units to see if any are dead and need to be cleaned up
   */
  public void deathCheck() {
    List<Unit> units = world.getUnits();
    for (int i=units.size()-1; i>=0; i--) {
      Unit unit = units.get(i);
      if (unit.getHp() <= 0) {
        units.remove(i);
        Log.d("ScriptTools", unit.getName()+" has died!");
      }
    } // for
  }

  /**
   * Checks this particular unit to see if it has died.
   * If it's dead it will be removed from the game
   */
  public void deathCheck(Unit unit) {
    if (unit.getHp() <= 0) {
      world.getUnits().remove(unit);
      Log.d("ScriptTools", unit.getName()+" has died!");
    }
  }

  /**
   * Applies the ability (runs the script)
   * self = selectedUnit
   * target = targetUnit
   */
  public boolean applyAbility(Ability ability, Unit self, Unit target) {
    if (self == null) {
      // Cannot apply ability without a self
      return false;
    }
    if (self.getAction() < ability.getActionCost()) {
      return false;
    }
    // Action cost
    self.setAction(self.getAction() - ability.getActionCost());
    // Execute the onStart
    scriptEngine.runActions(ability.getOnStart(), self, target);
    // Add effect
    Effect effect = ability.getEffect();
    if (effect != null) {
      // non-stackable effects can only be applied once
      if (effect.isStackable() || WorldUtils.effectFound(target.getEffects(), effect)) {
        target.getEffects().add(effect.clone());
      }
    }
    Log.d("ScriptTools", self.getName()+" did "+ability.getName()+" to "+target.getName());
    deathCheck(target);
    scriptEngine.runActions(world.getTriggers().getAbilityUsed(), self, target);
    return true;
  }

  /**
   * @return true if self is in range and able to attack target
   */
  public boolean canAttack(Unit self, Unit target) {
    if ((self == null) || (target == null) || (self.getAbilities().size() == 0)) {
      return false;
    }
    for (Ability ability : self.getAbilities()) {
      // in range of the ability
      if (self.getPos().distanceTo(target.getPos()) <= ability.getRange()) {
        // The ability applies
        if (scriptEngine.runRule(ability.getApplies(), self, target)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @return the first ability that is in range and applies to the target
   */
  public Ability usableAbility(Unit self, Unit target) {
    if ((self == null) || (target == null) || (self.getAbilities().size() == 0)) {
      return null;
    }
    for (Ability ability : self.getAbilities()) {
      if ((self.getAction() >= ability.getActionCost()) && // Have enough action points to use ability
          (self.getPos().distanceTo(target.getPos()) <= ability.getRange())) { // and in range of the ability
        // And the ability applies
        if (scriptEngine.runRule(ability.getApplies(), self, target)) {
          return ability;
        }
      }
    }
    return null;
  }

  /**
   * This will use the first ability that is in range and applies
   * @return true if the attack occurred
   */
  public boolean attack(Unit self, Unit target) {
    if ((self == null) || (target == null)) {
      return false;
    }
    List<Ability> abilities = self.getAbilities();
    if (abilities.size() == 0) {
      return false;
    }
    Ability ability = usableAbility(self, target);
    if (ability == null) {
      return false;
    }
    return applyAbility(ability, self, target);
  }

  /**
   * Moves the unit to the specified location
   */
  public boolean moveTo(Unit self, Position pos) {
    if ((self == null) || (pos == null)) {
      return false;
    }
    if (self.getAction() < self.getMoveActionCost()) {
      return false;
    }
    if (!isValidPosition(pos, self.getMoveRestrict(), true)) {
      return false;
    }
    self.setAction(self.getAction() - self.getMoveActionCost());
    self.setPos(pos);
    Log.d("ScriptTools", self.getName()+" moved to "+pos);
    scriptEngine.runActions(world.getTriggers().getAbilityUsed(), null, null);
    return true;
  }

  /**
   * Attacks the nearest enemy.
   * @return true if an enemy was attacked
   *         false if no enemy was attacked (not in range, not enough action points, ability didn't apply)
   */
  public boolean attackNearestEnemy(Unit self) {
    List<Unit> visibleEnemies = getVisibleOthers(self);
    for (Unit target : visibleEnemies) {
      Ability ability = usableAbility(self, target);
      if (ability != null) {
        applyAbility(ability, self, target);
        return true;
      }
    } // for
    return false;
  }

  /**
   * Moves towards the nearest visible enemy
   */
  public boolean moveTowardNearestEnemy(Unit self) {
    Unit enemy = getClosestVisibleEnemy(self);
    if (enemy == null) {
      return false;
    }
    Map<Position, BgTile> validPositions = unrestrictedPositions(self.getMoveRestrict(), true);
    Log.d("ScriptTools", "validPositions = "+validPositions);
    List<Position> path = self.getPos().pathTo(enemy.getPos(), validPositions, false);
    if (path.size() == 0) {
      return false;
    }
    Position newPos = null;
    // Enemy is near enough to get next to
    if (path.size() <= self.getMoveRange()) {
      newPos = path.get(path.size()-1);
    } else { // path.size > moveRange
      newPos = path.get(self.getMoveRange());
    }
    return moveTo(self, newPos);
  }

}
