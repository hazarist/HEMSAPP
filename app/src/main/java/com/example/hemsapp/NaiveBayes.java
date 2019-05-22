package com.example.hemsapp;

public class NaiveBayes {

    private String[] types = {"Kitchen","Garden", "Room"};
    private String[] evaluation = {"Good","Average", "Bad"};
    private String[] priorities = {"Urgent","Important","Standard"};

        public String NaiveBayes(Instance[] Instances,int counterIns,String[] employees,int counterEmployee,Task task) {


            RateObject[] totalProbabilities = new RateObject[counterEmployee];

            RateObject[] typeProbabilities = getProbabilities(employees,counterEmployee,types,Instances,counterIns,types.length*counterEmployee,0);
            RateObject[] priorityProbabilities = getProbabilities(employees,counterEmployee,priorities,Instances,counterIns,priorities.length*counterEmployee,1);
            RateObject[] evaluationProbabilities = getProbabilities(employees,counterEmployee,evaluation,Instances,counterIns,evaluation.length*counterEmployee,2);

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
                double typeProbability = getProbability(typeProbabilities,    task.getType(), employees[i]);
                double priorityProbability = getProbability(priorityProbabilities, task.getPriority(), employees[i]);
                double evaluationProbability = getProbability(evaluationProbabilities, "Good", employees[i]);
                po[i] = new RateObject(employees[i],( typeProbability == 0 ? 1 : typeProbability /totalProbabilities[i].getRate())
                        *(priorityProbability == 0 ? 1 : priorityProbability/totalProbabilities[i].getRate())
                        *(evaluationProbability == 0 ? 1 : evaluationProbability/totalProbabilities[i].getRate())
                        *(totalProbabilities[i].getRate()/Instances.length));
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
                            if (attributes[j].equals(Instances[j2].getSubtype()) && employees[i].equals(Instances[j2].getByWho())) {
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
