#include <iostream>
#include <fstream>
#include <cstdlib>
using namespace std;
int a[5000000];
struct table_info{
    int *p;
    int size;
    int num;
};
table_info *insert(table_info *table, int description){
    if (table->size == 0){
        table->p = new int[1];
        table->size = 1;
    }
    if (table->num == table->size){
        int *ntable = new int[2 * table->size];
        for (int i = 0; i < table->num; i++){
            ntable[i] = table->p[i];
        }
        delete[] table->p;
        table->p = ntable;
        table->size = 2 * table->size;
    }
    table->p[table->num] = description;
    table->num++;
    return table;
}
// You can add a parameter to insert the actual flower infos
table_info *multi_insert(table_info *table, int n){
    int t = table->num;
    for (int i = 0; i < n; i++){
        insert(table, t + i);
    }
    return table;
}
table_info *remove(table_info *table){
    if (table->size == 0){
        return table;
    }
    if (table->num <= table->size / 2){
        int *ntable = new int[table->size / 2];
        for (int i = 0; i < table->num; i++){
            ntable[i] = table->p[i];
        }
        delete[] table->p;
        table->p = ntable;
        table->size = table->size / 2;
    }
    table->num--;
    return table;
}
table_info *multi_remove(table_info *table, int n){
    for (int i = 0; i < n; i++) {
        remove(table);
    }
    return table;
}
int query(table_info *table, int i){
    if (i >= table->num) return 0;
    return table->p[i];
}
int main() {
    int op, num;
    ifstream fin("data.txt");
    clock_t start, stop;
    table_info t = {nullptr, 0, 0};
    cin>>op>>num;
    if(op == 4){
        int n;
        fin>>n;
        multi_insert(&t, n);
        for(int i = 0; i < n; i++) fin>>a[i];
        start = clock();
        for(int i = 0; i < n; i++){
            query(&t, a[i]);
        }
        stop = clock();
        cout<<"Time: "<<(double)(stop - start) / CLOCKS_PER_SEC<<endl;
        fin.close();
    }
    //op: 1 - insert; 2 - delete; 3 - query; 0 - exit
    else while (op != 0) {
        if (op == 1){
            start = clock();
            multi_insert(&t, num);
            stop = clock();
            cout<<"Capacity: "<<t.size<<endl;
            cout<<"Time: "<<(double)(stop - start) / CLOCKS_PER_SEC<<endl;
        }
        if (op == 2){
            start = clock();
            multi_remove(&t, num);
            stop = clock();
            cout<<"Capacity: "<<t.size<<endl;
            cout<<"Time: "<<(double)(stop - start) / CLOCKS_PER_SEC<<endl;
        }
        if (op == 3){
            start = clock();
            int r = query(&t, num);
            stop = clock();
            cout<<"description: "<<r<<endl;
            cout<<"Time: "<<(double)(stop - start) / CLOCKS_PER_SEC<<endl;
        }
        cin >> op >> num;
    }
    return 0;
}
