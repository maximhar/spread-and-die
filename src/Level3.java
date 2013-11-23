import java.util.Random;
/* a quite smart level AI - (doesn't really do more steps than Level2)
   the player attempts to change the region of the cell that is closest to
   the disease to a region different from the region he is on
   in case the disease is immediately next to the player, running away is given priority
   in case the disease is too far, the player moves randomly and changes regions around it
 */
public class Level3 extends Level {
    private boolean moveTurn;
    private Random randomizer;
    public Level3(Grid grid) {
        super(grid);
        this.randomizer = new Random();
    }

    @Override
    public String getName() {
        return "Level 3";
    }

    @Override
    protected void movePlayer() {
        try{
            this.moveTurn = !this.moveTurn;
            PlayerRelator relator = new PlayerRelator(this.player);
            changeRegion(relator);
        } catch (PlayerOutOfBoundsException ex){
            ex.printStackTrace();
        }
    }

    private void changeRegion(PlayerRelator relator) throws PlayerOutOfBoundsException {
        if(this.player.currentCell().isNearDisease()) {
            //escaping the disease is more important than changing regions
            movePlayer(relator);
            return;
        }
        //if we are in no immediate danger, see if there are any regions worth changing
        for(int cell = 0; cell < relator.cellCount(); cell++){
            PlayerRelator.CellRelator cellRelator = relator.getCellRelator(cell);
            // if the cell's region is the same as the player's cell region,
            // change it so the disease spreads slower
            if(!cellRelator.getCell().isBorder()
                    && cellRelator.getCell().getRegion() == this.player.currentCell().getRegion()) {
                cellRelator.getCell().setRegion(findBestRegion(player.currentCell().getRegion()));
                break;
            }
        }
        //in case of no suitable region to change, move randomly around
        movePlayerRandomly(relator);
    }

    private void movePlayer(PlayerRelator relator) throws PlayerOutOfBoundsException {
        for(int cell = 0; cell < relator.cellCount(); cell++){
            PlayerRelator.CellRelator cellRelator = relator.getCellRelator(cell);
            if(!cellRelator.getCell().isBorder()
                    && cellRelator.getCell().isNearDisease()
                    && !cellRelator.getOpposite().getCell().isDiseased()
                    && !cellRelator.getOpposite().getCell().isBorder()){
                relator.movePlayerTo(cellRelator.getOpposite());
                break;
            }
        }
    }

    private void movePlayerRandomly(PlayerRelator relator) throws PlayerOutOfBoundsException{
        PlayerRelator.CellRelator randomCell = relator.getCellRelator(this.randomizer.nextInt(relator.cellCount()));
        if(randomCell.getCell().isBorder()){
            relator.movePlayerTo(randomCell.getOpposite());
        }
        //don't stray too close to disease
        else if (!randomCell.getCell().isNearDisease()){
            relator.movePlayerTo(randomCell);
        }
    }

    private int findBestRegion(int playerRegion) {
        return (this.grid.getRegions()) % (playerRegion + 1);
    }
}
