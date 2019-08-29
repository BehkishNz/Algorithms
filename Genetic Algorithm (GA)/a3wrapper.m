GENERATION = [5 150 300];
POPULATION = [5 50 100];

section_1result = [];
i = 1;
while i <= 3
   j = 1;
   while j <= 3
       g = GENERATION(1, i);
       p = POPULATION(1, j);
       section_1result = [section_1result; g p 0.6 0.25 a3q1ResultGen(p, g, 0.6, 0.25)];
       j = j + 1;
   end
   i = i + 1;
end