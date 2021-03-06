package CalabashBrosVSMobs;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BattleField {//战场，保存了每一个单位和棋盘信息
    private int maxRow;
    private int maxColumn;
    private CalabashBros calabashBros;
    private GoodBeing grandpa;
    private EvilBeing snakeQueen;
    private EvilBeing[] mobs;
    private Board board;
    BattleField(int maxRow, int maxColumn, boolean debug){
        this.maxColumn=maxColumn;
        this.maxRow=maxRow;
        board=new Board(maxRow,maxRow);
        calabashBros=new CalabashBros();
        grandpa=new GoodBeing("爷爷");
        snakeQueen=new EvilBeing("蛇精");
        randomRearrange(debug);
    }
    public void randomRearrange(boolean debug){//随机选择一个阵形并在场上随机放置
        calabashRearrange(debug);
        mobsRearrange(debug);
    }
    private void calabashRearrange(boolean debug){//在战场左半边随机选择位置并放置葫芦娃
        for(int i=0;i<calabashBros.getBroNum();i++){
            board.deleteObject(calabashBros.getCalabashKid(i).getPos().getX(),calabashBros.getCalabashKid(i).getPos().getY());
        }
        board.deleteObject(grandpa.getPos().getX(),grandpa.getPos().getY());
        int mid=maxColumn/2;
        Random rand=new Random();
        if(calabashBros.getFormation().getMaxRow()>maxRow||calabashBros.getFormation().getMaxColumn()>maxColumn/2){
            System.out.println("BattleField too small, cannot initialize");
            return;
        }
        Position calabashStart=new Position(rand.nextInt(mid+1-calabashBros.getFormation().getMaxColumn()),
                rand.nextInt(maxRow+1-calabashBros.getFormation().getMaxRow()));
        if(debug)
            System.out.println("CalabashBros start pos: "+calabashStart.getX()+", "+calabashStart.getY());
        calabashBros.bubbleSort(false);
        for(int i=0;i<calabashBros.getBroNum();i++){
            Position pos=calabashBros.getFormation().getPosition(i).addPosWithoutChange(calabashStart);
            calabashBros.changeOnePos(pos.getX(),pos.getY(),i);
            board.addObject(pos.getX(),pos.getY(),calabashBros.getCalabashKid(i));
        }
        grandpaRearrange();
    }
    private void grandpaRearrange(){//在左边半场没有葫芦娃的地方放置爷爷
        Random rand=new Random();
        int mid=maxColumn/2;
        int x=rand.nextInt(mid);
        int y=rand.nextInt(maxRow);
        while(board.getObject(x,y)!=null){
            x=rand.nextInt(mid);
            y=rand.nextInt(maxRow);
        }
        grandpa.changePos(x,y);
        board.addObject(x,y,grandpa);
    }
    public void mobsRearrange(boolean debug){//在右边半场放置怪物
        if(mobs!=null){
            for(int i=0;i<mobs.length;i++){
                board.deleteObject(mobs[i].getPos().getX(),mobs[i].getPos().getY());
            }
            board.deleteObject(snakeQueen.getPos().getX(),snakeQueen.getPos().getY());
        }
        int midColumn=maxColumn/2;
        Random rand=new Random();
        int mobForNum=rand.nextInt(Formation.values().length);
        Formation formation=Formation.values()[mobForNum];
        while(formation.getMaxRow()>maxRow||formation.getMaxColumn()>maxColumn/2){
            mobForNum=rand.nextInt(Formation.values().length);
            formation=Formation.values()[mobForNum];
        }
        if(debug)
            System.out.println("mobs' formation number: "+mobForNum);
        int mobNum=formation.getUnitNum();
        mobs=new EvilBeing[formation.getUnitNum()];
        for(int i=0;i<mobNum;i++){//随机放置蝎子精或小喽啰
            if(rand.nextInt(2)==0)
                mobs[i]=new EvilBeing("蝎子精");
            else
                mobs[i]=new EvilBeing("小喽啰");
        }
        Position mobStart=new Position(rand.nextInt(maxColumn-midColumn+1-formation.getMaxColumn())+midColumn,
                rand.nextInt(maxRow+1-formation.getMaxRow()));
        if(debug)
            System.out.println("mob start num: "+mobStart.getX()+", "+mobStart.getY());
        for(int i=0;i<mobNum;i++){
            Position pos=formation.getPosition(i).addPosWithoutChange(mobStart);
            if(debug)
                System.out.println("mob no."+i+"'s position: "+pos.getX()+", "+pos.getY());
            mobs[i].changePos(pos.getX(),pos.getY());
            board.addObject(pos.getX(),pos.getY(),mobs[i]);
        }
        queenRearrange();
    }
    private void queenRearrange(){
        Random rand=new Random();
        int mid=maxColumn/2;
        int x=rand.nextInt(maxColumn-mid)+mid;
        int y=rand.nextInt(maxRow);
        while(board.getObject(x,y)!=null){
            x=rand.nextInt(maxColumn-mid)+mid;
            y=rand.nextInt(maxRow);
        }
        snakeQueen.changePos(x,y);
        board.addObject(x,y,snakeQueen);
    }
    public void printBattleField(){
        for(int i=0;i<maxRow;i++){
            for(int j=0;j<maxColumn;j++){
                Object a=board.getObject(j,i);
                if(a==null) {
                    System.out.print("O\t\t");
                }
                else{
                    Being b=(Being)a;
                    System.out.print(b.getName());
                    if(b.getName().length()<=2)
                        System.out.print("\t\t");
                    else
                        System.out.print("\t");
                }
                if(j==(maxColumn-1)/2)
                    System.out.print("|\t\t");
            }
            System.out.print("\n");
        }
    }
}

class Board{//棋盘的对象，使用map，可在其中更改每个位置上的物体
    private int maxRow;
    private int maxColumn;
    private Map map;
    Board(int row,int column){
        maxRow=row;
        maxColumn=column;
        map=new HashMap();
    }
    public void addObject(int x, int y, Object object){
        if(y>=maxRow||x>=maxColumn||y<0||x<0)
            return;
        map.put(getSingleDimensionPos(x,y),object);
    }
    public Object getObject(int x, int y){
        return map.get(getSingleDimensionPos(x,y));
    }
    public Object deleteObject(int x, int y){
        Object object=map.get(getSingleDimensionPos(x,y));
        map.remove(getSingleDimensionPos(x,y));
        return object;
    }
    public void clearBoard() {
        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < maxColumn; j++) {
                map.remove(getSingleDimensionPos(j, i));
            }
        }
    }
    private int getSingleDimensionPos(int x, int y){
        return x+y*maxColumn;
    }
}

enum Formation {//阵形，规定阵形的规格（长和宽），每一个点代表阵形中的一个人
    CRANEWINGS(new Position[]{new Position(0,3),
            new Position(1,2), new Position(1,4),
            new Position(2,1), new Position(2,5),
            new Position(3,0), new Position(3,6)
    }),GEESE(new Position[]{new Position(0,0),
            new Position(1,1),new Position(2,2),
            new Position(3,3),new Position(4,4)
    }),YOKE(new Position[]{new Position(0,0),
            new Position(1,1),new Position(0,2),
            new Position(1,3),new Position(0,4),
            new Position(1,5),
    }),LONGSNAKE(new Position[]{new Position(0, 0),
            new Position(0, 1), new Position(0, 2),
            new Position(0, 3), new Position(0, 4),
            new Position(0, 5), new Position(0, 6),
    }),FISHSCALE(new Position[]{new Position(0, 2),
            new Position(1, 1), new Position(1, 2),
            new Position(1, 3), new Position(2, 0),
            new Position(2, 1), new Position(2, 2),
            new Position(2, 3), new Position(2, 4),
            new Position(3, 1), new Position(3, 2),
            new Position(3, 3)
    }),SQUARE(new Position[]{new Position(0, 2),
            new Position(1, 1), new Position(1, 3),
            new Position(2, 4), new Position(2, 0),
            new Position(3, 1), new Position(3, 3),
            new Position(4, 2)
    }),CRESCENT(new Position[]{new Position(0, 2),
            new Position(0, 3), new Position(0, 4),
            new Position(1, 1), new Position(1, 5),
            new Position(2, 0), new Position(2, 6)
    }),ARROW(new Position[]{new Position(0, 2),
            new Position(1, 1), new Position(1, 2),
            new Position(1, 3), new Position(2, 0),
            new Position(2, 2), new Position(2, 4),
            new Position(3, 2), new Position(4, 2)
    }),;
    private int maxRow;
    private int maxColumn;
    private int unitNum;
    private Position[] formation;
    Formation(Position[] formation){
        this.formation=formation;
        unitNum=formation.length;
        int row=0;
        int column=0;
        for(int i=0;i<unitNum;i++){
            if(formation[i].getX()>column)
                column=formation[i].getX();
            if(formation[i].getY()>row)
                row=formation[i].getY();
        }
        maxRow=row+1;
        maxColumn=column+1;
    }
    public int getUnitNum() {
        return unitNum;
    }
    public int getMaxRow(){
        return maxRow;
    }
    public int getMaxColumn(){
        return maxColumn;
    }
    public final Position[] getFormation(){
        return formation;
    }
    public final Position getPosition(int num){
        return formation[num];
    }
}

class Position{//代表位置的对象
    private int x;
    private int y;
    Position(int x, int y){
        this.x=x;
        this.y=y;
    }
    public void changePos(int x, int y){
        this.x=x;
        this.y=y;
    }
    public int getX() {
        return x;
    }
    public int getY(){
        return y;
    }
    public void addX(int a){
        x+=a;
    }
    public void addY(int b){
        y+=b;
    }
    public final Position addPosWithChange(Position pos){//改变本位置，返回本位置
        x+=pos.x;
        y+=pos.y;
        return this;
    }
    public Position addPosWithoutChange(Position pos){//返回一个新的改变后的位置，本位置不改变
        Position res=new Position(x,y);
        return res.addPosWithChange(pos);
    }
    public boolean overZeroPoint(){//判断位置是否在第一象限
        if(x>0&&y>0)
            return true;
        else
            return false;
    }
}
