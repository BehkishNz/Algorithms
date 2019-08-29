% parameter range
% [ISE, t_r, t_s, M_p] = perffcn([P, I, D]);
% P (2, 18)    I (1.05, 9.42)    D (0.26, 2.37)
function [ result ] = a3q1ResultGen(population_limit, generation_limit, crossover_prob, mutation_prob)

tic;
POPULATION = population_limit;
GENERATION_LIMIT = generation_limit;


ISE = 100;

count = 0;
score = 0;

initial_samples = [];

while count < POPULATION
  P = (rand * 16) + 2;       %range(2,18)
  I = (rand * 8.37) + 1.05;  %range(1.05,9.42)
  D = (rand * 2.11) + 0.26;  %range(0.26,2.37)
  [ISE, t_r, t_s, M_p] = perffcn([P;I;D]);
  [generated_score] = fitter([ISE; t_r; t_s; M_p]);
  entry = [P I D ISE t_r t_s M_p generated_score];
  initial_samples = [initial_samples; entry];
  count = count + 1;
end

[~,idx] = sort(initial_samples(:,8), 'descend');
sorted_initial_samples = initial_samples(idx,:);
best_two_parents = sorted_initial_samples(1:2,:);

current_population = best_two_parents(1:2, :);


count = 2;

generation_tracker = [1 sorted_initial_samples(1,:)];
generation = 1;

while generation < GENERATION_LIMIT
    while count < POPULATION
      child = crossover(best_two_parents, crossover_prob);
      child = mutate(child, mutation_prob);
      P = child(1,1);
      I = child(1,2);
      D = child(1,3);
      [ISE, t_r, t_s, M_p] = perffcn([P;I;D]);
      [score] = fitter([ISE; t_r; t_s; M_p]);
      child_entry = [P I D ISE t_r t_s M_p score];
      current_population = [current_population; child_entry];
      count = count + 1;
    end
    count = 2;
    [~,idx] = sort(current_population(:,8), 'descend');
    sorted_current_population = current_population(idx,:);
    best_two_parents = sorted_current_population(1:2,:);
    current_population = best_two_parents(1:2, :);
    generation = generation + 1;
    generation_tracker = [generation_tracker; generation sorted_current_population(1,:)];
    fprintf('%d ',generation);
end

result = [toc generation_tracker(generation, 9)];



