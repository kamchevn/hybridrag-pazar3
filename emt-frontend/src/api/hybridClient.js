const BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

export async function askHybrid(query, model) {
  if (!query || typeof query !== 'string' || !query.trim()) {
    throw new Error('Внесете прашање.');
  }
  if (!model || typeof model !== 'string' || !model.trim()) {
    throw new Error('Изберете модел.');
  }

  const url =
    `${BASE_URL}/api/hybrid/ask` +
    `?q=${encodeURIComponent(query.trim())}` +
    `&model=${encodeURIComponent(model.trim())}`;

  let response;
  try {
    response = await fetch(url, {
      method: 'GET',
      headers: {
        Accept: 'application/json'
      }
    });
  } catch (networkErr) {
    throw new Error('Неуспешно поврзување со серверот. Обидете се повторно.');
  }

  let data;
  if (!response.ok) {
    const bodyText = await response.text().catch(() => '');
    const shortText = bodyText ? ` | ${bodyText.slice(0, 200)}` : '';
    throw new Error(`HTTP грешка ${response.status}${shortText}`);
  }
  try {
    data = await response.json();
  } catch (parseErr) {
    throw new Error('Неуспешно читање на одговорот (JSON).');
  }

  const answer = typeof data?.answer === 'string' ? data.answer : '';
  const urls = Array.isArray(data?.listings)
    ? data.listings
        .map((item) => item && item.url)
        .filter(Boolean)
    : [];
  const dedupedUrls = Array.from(new Set(urls));

  return {
    answer,
    urls: dedupedUrls,
    raw: data
  };
}



