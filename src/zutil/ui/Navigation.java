/*
 * Copyright (c) 2015 Ziver
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package zutil.ui;

import java.util.*;

/**
 * A class representing a navigation hierarchy/tree for an application.
 *
 * Created by Ziver on 2015-06-15.
 */
public class Navigation implements Iterable{
    private static final String NAVIGATION_URL_KEY = "i";
    private static int nextId = 0;
    private static HashMap<Integer, Navigation> navMap = new HashMap<Integer, Navigation>();

    private final Integer id;
    private String name;
    private int weight;
    private Object resource;

    private Navigation parentNav;
    private ArrayList<Navigation> subNav;



    private Navigation(String name) {
        this.id = nextId++;
        this.navMap.put(this.id, this);
        this.name = name;
        this.subNav = new ArrayList<>();
    }


    public List<Navigation> getSubNavs() {
        return subNav;
    }
    /**
     * Will create a new sub-nav if it does not already exist or return a existing one.
     */
    public Navigation createSubNav(String name) {
        Navigation nav = getSubNav(name);
        if(nav != null)
            return nav;

        nav = new Navigation(name);
        nav.setParentNav(this);
        subNav.add(nav);
        sortSubNavs();
        return nav;
    }
    /**
     * Searches for and returns the specified sub-nav or returns null if it was not found.
     */
    private Navigation getSubNav(String name) {
        for(Navigation nav : subNav) {
            if(nav.equals(name))
                return nav;
        }
        return null;
    }
    private void sortSubNavs(){
        Collections.sort(subNav, new Comparator<Navigation>() {
            @Override
            public int compare(Navigation o1, Navigation o2) {
                if (o1.weight == o2.weight)
                    return o1.name.compareToIgnoreCase(o2.name);
                return o1.weight - o2.weight;
            }
        });
    }
    @Override
    public Iterator iterator() {
        return subNav.iterator();
    }


    private void setParentNav(Navigation nav){
        this.parentNav = nav;
    }


    public String getName(){
        return name;
    }
    public String getUrl(){
        return "?"+NAVIGATION_URL_KEY+"="+this.id;
    }
    public Object getResource(){
        return resource;
    }


    /**
     * Assign a resource object specific to this navigation object.
     * This can be used if target page needs some additional information.
     */
    public Navigation setResource(Object obj){
        resource = obj;
        return this;
    }
    /**
     * Sets the weight of this navigation object. The weight is
     * used for deciding the order the parent will sort all sub navigation.
     * Lower values will be at the top of sub-nav list.
     */
    public Navigation setWeight(int weightOrder){
        this.weight = weightOrder;
        if(parentNav != null)
            parentNav.sortSubNavs();
        return this;
    }



    @Override
    public boolean equals(Object o){
        if(o instanceof String)
            return this.name.equals(o);
        return this == o ||
                (o != null && this.id == (((Navigation)o).id));
    }

    /**
     * Will create a clone of the navigation tree with some request instance specific information
     */
    public NavInstance createNavInstance(Map<String, String> request){
        return createNavInstance(getBreadcrumb(request));
    }
    private NavInstance createNavInstance(List<Navigation> activeList){
        NavInstance instance = new NavInstance(this);
        instance.setActive(activeList.contains(this));
        for (Navigation nav : subNav)
            instance.addSubNav(nav.createNavInstance(activeList));
        return instance;
    }



    public static Navigation createRootNav(){
        return new Navigation(null);
    }
    public static Navigation getRootNav(Map<String, String> request) {
        List<Navigation> breadcrumb = getBreadcrumb(request);
        if (!breadcrumb.isEmpty())
            return breadcrumb.get(0);
        return null;
    }

    /**
     * @return the specific Navigation object requested by client
     */
    public static Navigation getNavigation(Map<String, String> request) {
        if(request.containsKey(NAVIGATION_URL_KEY))
            return navMap.get(Integer.parseInt(request.get(NAVIGATION_URL_KEY)));
        return null;
    }

    /**
     * @param   request     A map of all url parameters sent from client
     * @return a List of Navigation objects depicting the navigation hierarchy for the
     *          requested page from the client. First entry will be the root navigation object.
     */
    public static List<Navigation> getBreadcrumb(Map<String, String> request) {
        LinkedList list = new LinkedList();
        Navigation current = getNavigation(request);
        if (current != null){
            while(current != null){
                list.addFirst(current);
                current = current.parentNav;
            }
        }
        return list;
    }




    public static class NavInstance{
        private Navigation nav;
        private boolean active;
        private ArrayList<NavInstance> subNavs;

        protected NavInstance(Navigation nav){
            this.nav = nav;
            this.subNavs = new ArrayList<>();
        }

        protected void setActive(boolean active){
            this.active = active;
        }
        protected void addSubNav(NavInstance subNav){
            subNavs.add(subNav);
        }

        public boolean isActive(){
            return active;
        }
        public List<NavInstance> getSubNavs() { return subNavs; }

        // Mirror getters from Navigation
        public String getName(){                 return nav.getName(); }
        public String getUrl(){                  return nav.getUrl(); }
        public Object getResource(){             return nav.getResource(); }


        public boolean equals(Object o){
            if (o instanceof Navigation)
                return nav.equals(o);
            else if (o instanceof NavInstance)
                return nav.equals(((NavInstance) o).nav);
            return false;
        }
    }
}
