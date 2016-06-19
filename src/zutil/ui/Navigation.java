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

import zutil.net.http.HttpHeader;

import java.util.*;

/**
 * A class representing a navigation hierarchy/tree for an application.
 *
 * Created by Ziver on 2015-06-15.
 */
public class Navigation implements Iterable{
    private static final String NAVIGATION_URL_KEY = "i";
    private static HashMap<String, Navigation> navMap = new HashMap<>();

    private final String id;
    private String name;
    private int weight;
    private Object resource;

    private Navigation parentNav;
    private ArrayList<Navigation> subNav;




    private Navigation(String id, String name) {
        if (id == null) this.id = ""+navMap.size();
        else            this.id = id;

        this.name = name;
        this.subNav = new ArrayList<>();

        this.navMap.put(this.id, this);
    }


    /**
     * Will return a existing Navigation or create a new one.
     *
     * @param   name    the nice String name of the destination
     */
    public Navigation createSubNav(String name) {
        return createSubNav(null, name);
    }
    /**
     * Will return a existing Navigation or create a new one.
     *
     * @param   id      the unique id or page name
     * @param   name    the nice String name of the destination
     */
    public Navigation createSubNav(String id, String name) {
        Navigation nav = getSubNav(id, name);
        if(nav != null)
            return nav;

        nav = new Navigation(id, name);
        nav.setParentNav(this);
        subNav.add(nav);
        sortSubNavs();
        return nav;
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
    /**
     * Searches for and returns the specified sub-nav with the id or name.
     * Returns null if no navigation object found.
     */
    private Navigation getSubNav(String id, String name) {
        for(Navigation nav : subNav) {
            if(nav.equals(id) || nav.equals(name))
                return nav;
        }
        return null;
    }
    @Override
    public Iterator iterator() {
        return subNav.iterator();
    }




    public String getName(){
        return name;
    }
    public Object getResource(){
        return resource;
    }



    private void setParentNav(Navigation nav){
        this.parentNav = nav;
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
    public NavInstance createParameterizedNavInstance(Map<String, String> request){
        return createParameterizedNavInstance(getBreadcrumb(getParameterizedNavigation(request)));
    }
    private NavInstance createParameterizedNavInstance(List<Navigation> activeList){
        NavInstance instance = new ParameterizedNavInstance(this);
        instance.setActive(activeList.contains(this));
        for (Navigation nav : subNav)
            instance.addSubNav(nav.createParameterizedNavInstance(activeList));
        return instance;
    }
    /**
     * @return the specific Navigation object requested by client
     */
    public static Navigation getParameterizedNavigation(Map<String, String> request) {
        if(request.containsKey(NAVIGATION_URL_KEY))
            return navMap.get(request.get(NAVIGATION_URL_KEY));
        return null;
    }
    

    public NavInstance createPagedNavInstance(HttpHeader header){
        Navigation nav = getPagedNavigation(header);
        if (nav != null)
            return createPagedNavInstance(getBreadcrumb(nav));
        return null;
    }
    private NavInstance createPagedNavInstance(List<Navigation> activeList){
        NavInstance instance = new PagedNavInstance(this);
        instance.setActive(activeList.contains(this));
        for (Navigation nav : subNav)
            instance.addSubNav(nav.createPagedNavInstance(activeList));
        return instance;
    }
    /**
     * @return the specific Navigation object requested by client
     */
    public static Navigation getPagedNavigation(HttpHeader header) {
        return navMap.get(header.getRequestPage());
    }



    public static Navigation createRootNav(){
        return new Navigation(null, null);
    }
    public static Navigation getRootNav(Map<String, String> request) {
        List<Navigation> breadcrumb = getBreadcrumb(getParameterizedNavigation(request));
        if (!breadcrumb.isEmpty())
            return breadcrumb.get(0);
        return null;
    }
    

    /**
     * @param   nav     the
     * @return a List of Navigation objects depicting the navigation hierarchy for the
     *          requested page from the client. First entry will be the root navigation object.
     */
    public static List<Navigation> getBreadcrumb(Navigation nav) {
        LinkedList list = new LinkedList();
        if (nav != null){
            while(nav != null){
                list.addFirst(nav);
                nav = nav.parentNav;
            }
        }
        return list;
    }




    public abstract static class NavInstance{
        protected Navigation nav;
        protected boolean active;
        protected ArrayList<NavInstance> subNavInstance;

        protected NavInstance(Navigation nav){
            this.nav = nav;
            this.subNavInstance = new ArrayList<>();
        }

        protected void setActive(boolean active){
            this.active = active;
        }
        protected void addSubNav(NavInstance subNav){
            subNavInstance.add(subNav);
        }

        public boolean isActive(){
            return active;
        }
        public List<NavInstance> getSubNavs() { return subNavInstance; }

        // Mirror getters from Navigation
        public String getName(){                 return nav.getName(); }
        public Object getResource(){             return nav.getResource(); }

        public abstract String getURL();


        public boolean equals(Object o){
            if (o instanceof Navigation)
                return nav.equals(o);
            else if (o instanceof NavInstance)
                return nav.equals(((NavInstance) o).nav);
            return false;
        }
    }

    public static class ParameterizedNavInstance extends NavInstance{
        protected ParameterizedNavInstance(Navigation nav) { super(nav); }

        public String getURL(){
            return "?"+NAVIGATION_URL_KEY+"="+nav.id;
        }
    }

    public static class PagedNavInstance extends NavInstance{
        protected PagedNavInstance(Navigation nav) { super(nav); }

        public String getURL(){
            return "/" + nav.id;
        }
    }
}
