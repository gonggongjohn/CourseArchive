#include <iostream>
#include <fstream>
#include <cmath>
#include <cstring>
#include <map>
#define R 6371.004
#define SUP 100000005
using namespace std;
struct city{
    string name;
    double longitude;
    double latitude;
};
struct edge{
    int from;
    int to;
    double weight;
};
double rad(double angle){
    return angle * M_PI / 180;
}
//Only correct when the positions are on the north-eastern hemisphere
double earth_dist(city *pos_a, city *pos_b){
    double long_a = rad(pos_a->longitude);
    double lat_a = rad(pos_a->latitude);
    double long_b = rad(pos_b->longitude);
    double lat_b = rad(pos_b->latitude);
    double c = sin(lat_a) * sin(lat_b) + cos(lat_a) * cos(lat_b) * cos(long_b - long_a);
    return R * acos(c);
}
int main() {
    string north_province[15] = {"济南市", "石家庄市", "天津市", "北京市", "沈阳市",
                                 "长春市", "哈尔滨市", "郑州市", "太原市", "西安市",
                                 "呼和浩特市", "兰州市", "银川市", "西宁市", "乌鲁木齐市"};
    map<int, int> region_map;
    city c[35];
    edge chosen[35], low_cost[35];
    double graph[35][35] = {SUP};
    int visit[35] = {0}, flag = 0, n = 0, last_visit, cnt = 0, north_tag;
    ifstream fin("position.txt");
    while (!fin.eof()){
        fin>>c[n].name>>c[n].longitude>>c[n].latitude;
        north_tag = 0;
        for (int i = 0; i < 15; i++){
            if (north_province[i] == c[n].name){
                region_map[n] = 0;
                north_tag = 1;
                break;
            }
        }
        if (north_tag == 0) region_map[n] = 1;
        n++;
    }
    //Build the graph
    for(int i = 0; i < n; i++)
        for(int j = 0; j < n; j++)
            if(i == j) graph[i][j] = 0;
            else {
                double dist = earth_dist(&c[i], &c[j]);
                graph[i][j] = dist;
                graph[j][i] = dist;
            }
    for(int i = 0; i < n; i++){
        low_cost[i] = {-1, -1, SUP};
    }
    visit[0] = 1;
    last_visit = 0;
    //Connect northern city
    while (!flag){
        flag = 1;
        for(int i = 0; i < n; i++){
            if(region_map[i] == 0 && visit[i] == 0 && graph[last_visit][i] < low_cost[i].weight){
                low_cost[i].from = last_visit;
                low_cost[i].to = i;
                low_cost[i].weight = graph[last_visit][i];
            }
        }
        int min_edge_idx = -1;
        double min_edge_dist = SUP;
        for (int i = 0; i < n; i++) {
            if(low_cost[i].weight != -1.0 && low_cost[i].weight < min_edge_dist){
                min_edge_dist = low_cost[i].weight;
                min_edge_idx = i;
            }
        }
        chosen[cnt] = low_cost[min_edge_idx];
        visit[min_edge_idx] = 1;
        low_cost[min_edge_idx] = {-1, -1, -1.0};
        last_visit = min_edge_idx;
        cnt++;
        for(int i = 0; i < n; i++){
            if(region_map[i] == 0 && visit[i] == 0){
                flag = 0;
                break;
            }
        }
    }
    flag = 0;
    for(int i = 0; i < n; i++){
        low_cost[i] = {-1, -1, SUP};
    }
    visit[12] = 1;
    last_visit = 12;
    //Connect southern city
    while (!flag){
        flag = 1;
        for(int i = 0; i < n; i++){
            if(region_map[i] == 1 && visit[i] == 0 && graph[last_visit][i] < low_cost[i].weight){
                low_cost[i].from = last_visit;
                low_cost[i].to = i;
                low_cost[i].weight = graph[last_visit][i];
            }
        }
        int min_edge_idx = -1;
        double min_edge_dist = SUP;
        for (int i = 0; i < n; i++) {
            if(low_cost[i].weight != -1.0 && low_cost[i].weight < min_edge_dist){
                min_edge_dist = low_cost[i].weight;
                min_edge_idx = i;
            }
        }
        chosen[cnt] = low_cost[min_edge_idx];
        visit[min_edge_idx] = 1;
        low_cost[min_edge_idx] = {-1, -1, -1.0};
        last_visit = min_edge_idx;
        cnt++;
        for(int i = 0; i < n; i++){
            if(region_map[i] == 1 && visit[i] == 0){
                flag = 0;
                break;
            }
        }
    }
    int min_from, min_to;
    double min_dist = SUP;
    for (int i = 0; i < n; i++)
        for(int j = 0; j < i; j++){
            int spec = (region_map[i] == 0 && region_map[j] == 1) || (region_map[i] == 1 && region_map[j] == 0);
            if (spec && graph[i][j] < min_dist){
                min_from = i;
                min_to = j;
                min_dist = graph[i][j];
            }
        }
    chosen[cnt].from = min_from;
    chosen[cnt].to = min_to;
    chosen[cnt].weight = min_dist;
    cnt++;
    double sum = 0.0;
    for(int i = 0; i < cnt; i++){
        cout<<c[chosen[i].from].name<<"->"<<c[chosen[i].to].name<<" "<<chosen[i].weight<<"km"<<endl;
        sum += chosen[i].weight;
    }
    cout<<"总长度: "<<sum<<"km"<<endl;
    return 0;
}
