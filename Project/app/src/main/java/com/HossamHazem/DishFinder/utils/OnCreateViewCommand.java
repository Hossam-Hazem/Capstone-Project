package com.HossamHazem.DishFinder.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by Hossam on 9/10/2017.
 */

public abstract class OnCreateViewCommand {
       public abstract void run();
       public static void execute(Collection<OnCreateViewCommand> onCreateViewCommands){
           Iterator<OnCreateViewCommand> iterator = onCreateViewCommands.iterator();
           while(iterator.hasNext()){
               iterator.next().run();
               iterator.remove();
           }
       }
}
