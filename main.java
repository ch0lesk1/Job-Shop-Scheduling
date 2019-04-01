import java.util.Random;
import java.util.Arrays;

public class main {


    public static int fitness(int[] pop,int[][] job,int[][] machine,int n,int m,int[] wp,int wpnum)
    {
        //得到调度对应的机器序列
        int[] schedual=new int[wpnum];//调度对应的机器序列
        int[] temp=new int[n];//对应工件的计数器数组
        for(int i=0;i<wpnum;i++){
            schedual[i]=machine[pop[i]][temp[pop[i]]];
            //System.out.print(schedual[i]);
            temp[pop[i]]=temp[pop[i]]+1;
        }

        int[][] endtime = new int[n][wp[0]];//工件每个操作对应的最大完工时间数组 n*m
        int[] freetime = new int[m];//每个机器最近空闲时间的数组 m
        int[] temp1 = new int[n];//对应工件的计数器数组
        for(int i=0;i<wpnum;i++){//遍历染色体对应的调度,当前操作的工件号对应为pop[i]=%i,在工件上的序号对应为temp1[pop[i]]=%j,对应机器号为schedual[i]
            if(temp1[pop[i]]>=1) {
                if (endtime[pop[i]][temp1[pop[i]] - 1] > freetime[schedual[i]]) {
                    endtime[pop[i]][temp1[pop[i]]] = endtime[pop[i]][temp1[pop[i]] - 1] + job[pop[i]][temp1[pop[i]]];
                    freetime[schedual[i]] = endtime[pop[i]][temp1[pop[i]]];
                    temp1[pop[i]]++;
                } else {
                    endtime[pop[i]][temp1[pop[i]]] = freetime[schedual[i]] + job[pop[i]][temp1[pop[i]]];
                    freetime[schedual[i]] = endtime[pop[i]][temp1[pop[i]]];
                    temp1[pop[i]]++;
                }
            }else{
                endtime[pop[i]][temp1[pop[i]]] = freetime[schedual[i]] + job[pop[i]][temp1[pop[i]]];
                freetime[schedual[i]] = endtime[pop[i]][temp1[pop[i]]];
                temp1[pop[i]]++;
            }
        }

        int max = 0;
        for(int i=0;i<n;i++){
            if(endtime[i][wp[i]-1]>max){
                max=endtime[i][wp[i]-1];
            }
        }
        return max;
    }

    //将个体按适应度值大小排序
    public static void sort(int[][] pop,int popsize,int[] tfitness){
        int temp=0;
        int[] mtemp=null;
        for(int i=0;i<popsize-1;i++){
            for(int j=0;j<popsize-1-i;j++){
                if(tfitness[j]>tfitness[j+1]){
                    temp=tfitness[j+1];
                    tfitness[j+1]=tfitness[j];
                    tfitness[j]=temp;
                    mtemp=pop[j+1];
                    pop[j+1]=pop[j];
                    pop[j]=mtemp;
                }
            }
        }
    }
    //选择算子
    public static void select(int[][] pop,int popsize,double gap,int[] wp,int wpnum,int n){
        //基于锦标赛,elite count=20
        int ecount=20;
        for(int i=0;i<popsize;i++){
            if(i>=ecount){
                newpop(pop[i],wp,wpnum,n);//随机生成新个体
            }
        }


    }
    //交叉算子
    public static void across(int[][] pop,int popsize,int wpnum,int n,double pc){
        Random ra = new Random();//生成随机数发生器
        int temp=0;
        if(ra.nextDouble()<=pc){
            for(int i=0;i<popsize;i++) {
                int apop = ra.nextInt(popsize);//另一个个体
                while (apop == i) {
                    apop = ra.nextInt(popsize);
                }
                int index = ra.nextInt(wpnum);
                while (pop[i][index] == pop[apop][index]) {
                    index = ra.nextInt(wpnum);
                }

                for (int j = 0; j < wpnum; j++) {//自交换
                    if (pop[i][j] == pop[apop][index]) {
                        pop[i][j] = pop[i][index];
                        break;
                    }
                }

                for (int j = 0; j < wpnum; j++) {//自交换
                    if (pop[apop][j] == pop[i][index]) {
                        pop[apop][j] = pop[apop][index];
                        break;
                    }
                }


                temp = pop[i][index];
                pop[i][index] = pop[apop][index];//互交换
                pop[apop][index] = temp;
            }
        }
    }
    //变异算子
    public static void aberrance(int[][] pop,int popsize,int wpnum,int n,double pm){
        Random ra = new Random();//生成随机数发生器
        int temp=0;
        if(ra.nextDouble()<=pm) {
            for (int i = 0; i < popsize; i++) {
                int index1 = ra.nextInt(wpnum);
                int index2 = ra.nextInt(wpnum);
                while (index1 == index2) {
                    index2 = ra.nextInt(wpnum);
                }
                temp = pop[i][index1];
                pop[i][index1] = pop[i][index2];
                pop[i][index2] = temp;
            }
        }
    }
    //生成新个体
    public static void newpop(int[] pop,int[] wp,int wpnum,int n){
        Random ra = new Random();//生成随机数发生器
        int[] wptemp = new int[wp.length];//对应工件的剩余操作数的计数器
        for(int j=0;j<wp.length;j++){
            wptemp[j]=wp[j];//计数器初始化
        }
        for (int j = 0; j < wpnum; j++) {
            int temp = ra.nextInt(n);//选择工序
            while (wptemp[temp] == 0) {
                temp = ra.nextInt(n);
            }
            pop[j] = temp;
            wptemp[temp] = wptemp[temp] - 1;
        }
    };

    public static void main(String[] args) {
        //初始化参数
        int[][] job;//加工时间
        int[][] machine;//工序约束
        int popsize = 1000;//种群规模,默认规模1000
        int[] tfitness = new int[popsize];//种群的适应度值向量
        int n = 0;//工件数量
        int m = 0;//机器数量
        int[] wp;//工序对应的操作数量的向量
        int wpnum = 0;//总共的操作数量
        double pc = 0.8;//交叉概率,默认值为0.8
        double pm = 0.05;//变异概率,默认值为0.05
        double gap = 1.0;//代沟，默认值为1.0
        int maxgen = 500;//最大代数,默认值为500
        int gen = 0;//目前的代数

        //算例1

        //job = new int[][]{{21, 43, 15},{12, 32, 36, 26, 18, 38,},{28, 57, 69, 37, 52},{ 34, 54, 66, 28, 37, 68, 45},{ 16, 34, 25},{ 9, 87, 24, 26, 39},{ 26, 62, 16, 24, 38, 48},{ 10, 29},{ 59, 87, 36, 34, 64, 26, 76}, {12, 26, 18, 26, 21}};
        //job = new int[]{21, 43, 15,12, 32, 36, 26, 18, 38, 28, 57, 69, 37, 52, 34, 54, 66, 28, 37, 68, 45,16, 34, 25, 9, 87, 24, 26, 39,26, 62, 16, 24, 38, 48,  10, 29, 59, 87, 36, 34, 64, 26, 76, 12, 26, 18, 26, 21};
        //machine = new int[][]{{1,2,4},{1,8,2,5,4,9},{1,8,2,6,9},{3,8,2,5,6,7,9},{1,2,9},{1,5,4,2,7},{3,8,1,5,6,9},{1,7},{3,2,1,4,5,6,7},{3,8,4,6,9}};
        //wp = new int[]{3, 6, 5, 7, 3, 5, 6, 2, 7, 5};//每个工件的操作数目

        //算例ft06
        job = new int[][]{{1, 3, 6, 7, 3, 6}, {8, 5, 10, 10, 10, 4}, {5, 4, 8, 9, 1, 7}, {5, 5, 5, 3, 8, 9}, {9, 3, 5, 4, 3, 1}, {3, 3, 9, 10, 4, 1}};
        //machine = new int[][]{{3, 1, 2, 4, 6, 5}, {2, 3, 5, 6, 1, 4}, {3, 4, 6, 1, 2, 5}, {2, 1, 3, 4, 5, 6}, {3, 2, 5, 6, 1, 4}, {2, 4, 6, 1, 5, 3}};//1-base
        machine = new int[][]{{2,0,1,3,5,4},{1,2,4,5,0,3},{2,3,5,0,1,4},{1,0,2,3,4,5},{2,1,4,5,0,3},{1,3,5,0,4,2}};//0-base
        wp = new int[]{6, 6, 6, 6, 6, 6};


        //数据预处理
        n = job.length;//获得工件数量
        m = machine.length;//获得机器数量
        System.out.println(n + "个工件，" + m + "个机器");
        for (int i = 0; i < wp.length; i++) {
            wpnum = wpnum + wp[i];
        }
        System.out.println("一共有" + wp.length + "个工序");
        System.out.println("一共有" + wpnum + "个操作");


        //程序开始

        Random ra = new Random();//生成随机数发生器

        System.out.println("开始生成种群");
        //用随机方法得到一个初始种群
        int[][] pop = new int[popsize][wpnum];//初始化种群数组
        for (int i = 0; i < popsize; i++) {
            int[] wptemp = new int[wp.length];//对应工件的剩余操作数的计数器
            for (int j = 0; j < wp.length; j++) {
                wptemp[j] = wp[j];//计数器初始化
            }
            for (int j = 0; j < wpnum; j++) {
                int temp = ra.nextInt(n);//选择工序
                while (wptemp[temp] == 0) {
                    temp = ra.nextInt(n);
                }
                pop[i][j] = temp;
                wptemp[temp] = wptemp[temp] - 1;
            }
        }
        System.out.println("种群初始化完成");
        System.out.println("计算适应度值");
        //计算初始种群的适应度值
        for (int i = 0; i < popsize; i++) {
            tfitness[i] = fitness(pop[i], job, machine, n, m, wp, wpnum);
            //System.out.println("适应度值为:"+tfitness[i]);
            //System.out.print("对应的调度为:");
            //for(int j=0;j<wpnum;j++){
                //System.out.print(pop[i][j]);
            //}
            //System.out.println();
        }


        int bestfitness = tfitness[0];
        int position = 0;
        for (int i = 0; i < popsize; i++) {
            if (tfitness[i] < bestfitness) {
                bestfitness = tfitness[i];
                position = i;
            }
        }
        System.out.println("目前最好适应度值为:" + bestfitness);
        System.out.print("最好个体对应的调度方案为:");
        for (int i = 0; i < wpnum; i++) {
            System.out.print(pop[position][i]);
        }
        System.out.println();


        while (gen < maxgen) {

            //按适应度值重新排列种群个体
            sort(pop,popsize,tfitness);
            //选择算子
            select(pop, popsize, gap, wp, wpnum, n);
            //交叉算子
            across(pop, popsize, wpnum, n, pc);
            //变异算子
            aberrance(pop, popsize, wpnum, n, pm);

            //更新适应度值向量
            for (int i = 0; i < popsize; i++) {
                tfitness[i] = fitness(pop[i], job, machine, n, m, wp, wpnum);
            }

            for (int i = 0; i < popsize; i++) {
                if (tfitness[i] < bestfitness) {
                    bestfitness = tfitness[i];
                    position = i;
                }
            }
            System.out.println("当前代最好个体水平为:" + bestfitness);
            System.out.print("最好个体对应的调度方案为:");
            for (int i = 0; i < wpnum; i++) {
                System.out.print(pop[position][i]);
            }
            System.out.println();

            gen = gen + 1;//代数增加1
        }


    }

}
