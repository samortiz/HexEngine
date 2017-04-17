package com.alwaysrejoice.hexengine.play;

import com.alwaysrejoice.hexengine.dto.Position;
import com.alwaysrejoice.hexengine.dto.Unit;
import com.alwaysrejoice.hexengine.dto.World;
import com.alwaysrejoice.hexengine.util.WorldUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Cached data for use by the AI
 */
public class AiTools {

  private World world;

  // key=teamId value=all the positions the team can see
  private Map<String, List<Position>> allTeamViews = new HashMap<>();

  // key=unitId value=all positions visible to this unit
  private Map<String, List<Position>> allUnitViews = new HashMap<>();


  public AiTools(World world) {
    this.world = world;
  }

  /**
   * Updates the cache for this unit
   */
  public void unitChanged(Unit unit) {
    // Clear the team cache
    allTeamViews.put(unit.getTeamId(), null);
    // Clear the unit cache
    allUnitViews.put(unit.getId(), null);
  }


  /**
   * @return all the positions visible to any unit on the team
   */
  public List<Position> getTeamView(String teamId) {
    List<Position> teamView = allTeamViews.get(teamId);
    if (teamView != null) {
      return teamView;
    }
    // Using a set to remove duplicates
    Set<Position> teamViewSet = new HashSet<>();
    // Add together the views of every member on the team
    for (Unit unit : getTeamUnits(teamId)) {
      teamViewSet.addAll(getUnitView(unit));
    }
    teamView = new ArrayList<>();
    teamView.addAll(teamViewSet);
    allTeamViews.put(teamId, teamView);
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
   * @return All the positions visible to the unit
   */
  public List<Position> getUnitView(Unit unit) {
    List<Position> unitView = allUnitViews.get(unit.getId());
    if (unitView != null) {
      return unitView;
    }
    unitView = getVisiblePositions(unit);
    allUnitViews.put(unit.getId(), unitView);
    return unitView;
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
        if (!WorldUtils.isValidPosition(linePos, unit.getSightRestrict(), false)) {
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


}
