package com.example.hemsapp;

public class NaiveBayes {

        public String NaiveBayes(Instance[] Instances,int counterIns,String[] employees,int counterEmployee,Task task) {

            String[] types = {"Room","Garden","Kitchen"};
            String[] evaluation = {"Good","Average"};
            String[] priorities = {"Urgent","Important","Standard"};

            RateObject[] totalProbabilities = new RateObject[counterEmployee];

            RateObject[] typeProbabilities = getProbabilities(employees,counterEmployee,types,Instances,counterIns,types.length*counterEmployee,0);
            RateObject[] priorityProbabilities = getProbabilities(employees,counterEmployee,priorities,Instances,counterIns,priorities.length*counterEmployee,1);
            RateObject[] timeProbabilities = getProbabilities(employees,counterEmployee,evaluation,Instances,counterIns,evaluation.length*counterEmployee,2);

            for(int i = 0; i < counterEmployee; i++) {
                double count = 0;
                String temp = employees[i];
                for (int j = 0; j < counterIns; j++){
                    if( Instances[j].getByWho().equals(temp)){
                        count++;
                    }
                }
                totalProbabilities[i] = new RateObject(employees[i],count);
            }

            RateObject[] po = new RateObject[counterEmployee];

            for (int i = 0; i < po.length; i++) {
                double a = getProbability(typeProbabilities,    task.getType(), employees[i]);
                double b = getProbability(priorityProbabilities, task.getPriority(), employees[i]);
                double c = getProbability(timeProbabilities, "good", employees[i]);
                po[i] = new RateObject(employees[i],(a/totalProbabilities[i].getRate())*(b/totalProbabilities[i].getRate())*(c/totalProbabilities[i].getRate())*(totalProbabilities[i].getRate()/Instances.length));
            }

            int maxIndex = 0;
            for(int i = 0; i < po.length -1 ; i++) {
                if(po[maxIndex].getRate() < po[i+1].getRate() && po[i].getRate() != 0) {
                    maxIndex = i+1;
                }
            }

            return po[maxIndex].getCurrent();
        }

    public static RateObject[] getProbabilities(String[] employees,int counterEmployee, String[] attributes, Instance[] Instances,int counterIns, int numberOfProbabilityObject, int c) {
        int count2 = 0;
        RateObject[] temp = new RateObject[numberOfProbabilityObject];
        for (int i = 0; i < counterEmployee; i++) {
            for (int j = 0; j < attributes.length; j++) {
                double count = 0;
                for (int j2 = 0; j2 < counterIns; j2++) {
                    switch (c) {
                        case 0:
                            if (attributes[j].equals(Instances[j2].getType()) && employees[i].equals(Instances[j2].getByWho())) {
                                count++;
                            }
                            break;
                        case 1:
                            if (attributes[j].equals(Instances[j2].getPriority()) && employees[i].equals(Instances[j2].getByWho())) {
                                count++;
                            }
                            break;
                        case 2:
                            if (attributes[j].equals(Instances[j2].getEvaluation()) && employees[i].equals(Instances[j2].getByWho())) {
                                count++;
                            }
                            break;
                        default:
                            break;
                    }
                }
                temp[count2] = new RateObject(attributes[j], employees[i], count);
                count2++;
            }
        }
        return temp;
    }

    public static double getProbability(RateObject[] probabilityObject, String value, String who) {
        for (int i = 0; i < probabilityObject.length; i++) {
            if (probabilityObject[i].getCurrent().equals(value) && probabilityObject[i].getTarget().equals(who))
                return probabilityObject[i].getRate();
        }
        return 0;

    }
}
